package com.istudio.player

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import androidx.media3.session.MediaController
import com.istudio.player.application.APP_TAG
import com.istudio.player.controllers.VideoMediaController
import com.istudio.player.controllers.VideoPlaybackController
import com.istudio.player.service.PlayerMediaSessionService
import com.istudio.player.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionController: VideoMediaController,
    private val playbackController: VideoPlaybackController,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var isServiceRunning = false

    private val _controllerState = mutableStateOf<MediaController?>(null)
    val controllerState: State<MediaController?> = _controllerState

    private val _playerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.PlayerIdle)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    init {
        initializePlayer()
    }

    override fun onCleared() {
        pauseVideo()
        stopVideo()
        super.onCleared()
    }

    private suspend fun initializeController() {
        val controller = sessionController.initialize()
        controller.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _playerState.value = PlayerState.PlayerBuffering
                    }
                    Player.STATE_READY -> {
                        _playerState.value = PlayerState.PlayerReady
                    }
                    Player.STATE_ENDED -> {
                        _playerState.value = PlayerState.PlayerEnded
                    }
                    Player.STATE_IDLE -> {
                        _playerState.value = PlayerState.PlayerEnded
                    }
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(APP_TAG, "isPlaying: $isPlaying")
                val playbackState = controller.playbackState
                val playWhenReady = controller.playWhenReady
                val suppressionReason = controller.playbackSuppressionReason
                val error = controller.playerError

                when {
                    error != null -> {
                        Log.e(APP_TAG, "Player error occurred: ${error.message}")
                        _playerState.value = PlayerState.PlayerError(error)
                    }

                    suppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                        Log.w(APP_TAG, "Playback suppressed. Reason: $suppressionReason")
                        _playerState.value = PlayerState.PlayerSuppressed(suppressionReason)
                    }

                    !playWhenReady -> {
                        Log.d(APP_TAG, "Player is paused or waiting.")
                        _playerState.value = PlayerState.PlayerPaused
                    }

                    isPlaying -> {
                        Log.d(APP_TAG, "Playback is active.")
                        _playerState.value = PlayerState.PlayerPlaying
                    }

                    else -> {
                        Log.d(APP_TAG, "Unknown playback state fallback.")
                        _playerState.value = PlayerState.PlayerIdle
                    }
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                when (val cause = error.cause) {
                    is HttpDataSource.HttpDataSourceException -> {
                        when (cause) {
                            is HttpDataSource.InvalidResponseCodeException -> {
                                Log.e(APP_TAG, "HTTP error code: ${cause.responseCode}")
                            }
                            else -> Log.e(APP_TAG, "HTTP error: ${cause.message}")
                        }
                    }
                    else -> Log.e(APP_TAG, "Playback error: ${error.message}")
                }
                _playerState.value = PlayerState.PlayerError(error)
            }
        })
        _controllerState.value = controller
        startNewMedia()
    }

    private fun playVideo() = playbackController.play()

    private fun pauseVideo() = playbackController.pause()

    private fun stopVideo() = sessionController.release()

    private fun startMediaService() {
        if (!isServiceRunning) {
            try {
                val intent = Intent(context, PlayerMediaSessionService::class.java)
                ContextCompat.startForegroundService(context, intent)
                isServiceRunning = true
                Log.d(APP_TAG, "Media service started successfully")
            } catch (e: Exception) {
                Log.e(APP_TAG, "Media service failed to start", e)
            }
        }
    }

    fun startNewMedia() {
        val videoUrl = Constants.VIDEO_URL
        val artworkUrl = Constants.ART_WORK_URL
        val title = "Title-1"
        val artist = "Artist-1"

        val mediaItem = MediaItem.Builder().setUri(videoUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtworkUri(artworkUrl.toUri())
                    .setArtist(artist).build()
            )
            .build()

        controllerState.value?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    private fun initializePlayer() {
        viewModelScope.launch {
            try {
                // Step 1: Start service
                startMediaService()
                // Step 2: Initialise media controller
                initializeController()
                // Step 3: Play video
                playVideo()
                Log.d(APP_TAG, "Playback started successfully")
            } catch (e: Exception) {
                Log.e(APP_TAG, "Error while starting playback", e)
            }
        }
    }
}

sealed class PlayerState {
    data object PlayerBuffering: PlayerState()
    data object PlayerReady: PlayerState()
    data object PlayerEnded: PlayerState()
    data object PlayerIdle: PlayerState()
    data object PlayerPlaying: PlayerState()
    data object PlayerPaused : PlayerState()
    data class PlayerError(val exception: PlaybackException) : PlayerState()
    data class PlayerSuppressed(val reason: Int) : PlayerState()
}