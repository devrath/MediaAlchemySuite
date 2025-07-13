@file:OptIn(SavedStateHandleSaveableApi::class)

package com.istudio.player

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.istudio.player.application.APP_TAG
import com.istudio.player.controllers.VideoMediaController
import com.istudio.player.controllers.VideoPlaybackController
import com.istudio.player.service.PlayerMediaSessionService
import com.istudio.player.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionController: VideoMediaController,
    private val playbackController: VideoPlaybackController
) : ViewModel() {

    private var isServiceRunning = false

    private val _controllerState = mutableStateOf<MediaController?>(null)
    val controllerState: State<MediaController?> = _controllerState

    override fun onCleared() {
        pauseVideo()
        stopVideo()
        super.onCleared()
    }

    suspend fun initializeController() {
        val controller = sessionController.initialize()
        controller.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> Log.d(APP_TAG, "Buffering")
                    Player.STATE_READY -> Log.d(APP_TAG, "Ready to play")
                    Player.STATE_ENDED -> Log.d(APP_TAG, "Playback ended")
                    Player.STATE_IDLE -> Log.d(APP_TAG, "Idle state")
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(APP_TAG, "Is playing: $isPlaying")
            }
        })
        _controllerState.value = controller
    }

    fun playVideo() = playbackController.play()

    private fun pauseVideo() = playbackController.pause()

    private fun stopVideo() = sessionController.release()

    fun startMediaService(context: Context) {
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
}