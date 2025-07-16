package com.istudio.player.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.session.MediaController
import com.istudio.player.application.APP_TAG
import com.istudio.player.callbacks.PlayerStateListener
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
import kotlinx.coroutines.flow.update

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionController: VideoMediaController,
    private val playbackController: VideoPlaybackController,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        initializePlayer()
    }

    override fun onCleared() {
        pauseVideo()
        stopVideo()
        super.onCleared()
    }

    fun onPlayPauseToggle() {
        _uiState.value.controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun onSeekBack() {
        _uiState.value.controller?.seekBack()
    }

    fun onSeekForward() {
        _uiState.value.controller?.seekForward()
    }

    fun onToggleCaptions() {
        _uiState.value.controller?.let { controller ->
            val newValue = !_uiState.value.captionsEnabled
            _uiState.update { it.copy(captionsEnabled = newValue) }

            controller.trackSelectionParameters = controller.trackSelectionParameters
                .buildUpon()
                .setPreferredTextLanguage("en")
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !newValue)
                .build()
        }
    }

    fun onPlaybackSpeedSelected(speed: Float) {
        _uiState.value.controller?.setPlaybackSpeed(speed)
    }

    @OptIn(UnstableApi::class)
    private suspend fun initializeController() {
        val controller = sessionController.initialize()
        controller.addListener(
            PlayerStateListener(controller) { state ->
                _uiState.update {
                    it.copy(
                        playerState = state,
                        subtitleLanguages = if (state is PlayerState.PlayerReady) listAvailableSubtitleLanguages() else it.subtitleLanguages,
                        audioLanguages = if (state is PlayerState.PlayerReady) listAvailableAudioLanguages() else it.audioLanguages
                    )
                }
            }
        )
        _uiState.update { it.copy(controller = controller) }
        startNewMedia()
    }

    private fun playVideo() = playbackController.play()

    private fun pauseVideo() = playbackController.pause()

    private fun stopVideo() = sessionController.release()

    private fun startMediaService() {
        if (!_uiState.value.isServiceRunning) {
            try {
                val intent = Intent(context, PlayerMediaSessionService::class.java)
                ContextCompat.startForegroundService(context, intent)
                _uiState.update { it.copy(isServiceRunning = true) }
                Log.d(APP_TAG, "Media service started successfully")
            } catch (e: Exception) {
                Log.e(APP_TAG, "Media service failed to start", e)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun startNewMedia() {
        try {
            val videoUrl = Constants.MULTI_LANG_AUDIO_SUBS
            val artworkUrl = Constants.ART_WORK_URL
            val title = "Title-1"
            val artist = "Artist-1"

            val mimeType = when {
                videoUrl.endsWith(".m3u8", ignoreCase = true) -> MimeTypes.APPLICATION_M3U8
                videoUrl.endsWith(".mp4", ignoreCase = true) -> MimeTypes.VIDEO_MP4
                else -> MimeTypes.VIDEO_UNKNOWN
            }

            val mediaItem = MediaItem.Builder()
                .setUri(videoUrl)
                .setMimeType(mimeType)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtworkUri(artworkUrl.toUri())
                        .setArtist(artist)
                        .build()
                )
                .build()

            _uiState.value.controller?.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun initializePlayer() {
        viewModelScope.launch {
            try {
                // Optional: startMediaService()
                initializeController()
                playVideo()
                Log.d(APP_TAG, "Playback started successfully")
            } catch (e: Exception) {
                Log.e(APP_TAG, "Error while starting playback", e)
            }
        }
    }

    fun onSubtitleLanguageSelected(language: String) {
        _uiState.update { it.copy(captionsEnabled = true) }
        _uiState.value.controller?.let { controller ->
            controller.trackSelectionParameters = controller.trackSelectionParameters
                .buildUpon()
                .setPreferredTextLanguage(language)
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                .build()
        }
    }

    @OptIn(UnstableApi::class)
    fun listAvailableSubtitleLanguages(): List<String> {
        val controller = _uiState.value.controller ?: return emptyList()
        return controller.currentTracks.groups
            .filter { it.type == C.TRACK_TYPE_TEXT }
            .flatMap { group ->
                (0 until group.length)
                    .mapNotNull { index -> group.getTrackFormat(index).language }
            }
            .distinct()
    }

    @OptIn(UnstableApi::class)
    fun listAvailableAudioLanguages(): List<String> {
        val controller = _uiState.value.controller ?: return emptyList()
        return controller.currentTracks.groups
            .filter { it.type == C.TRACK_TYPE_AUDIO }
            .flatMap { group ->
                (0 until group.length)
                    .mapNotNull { index -> group.getTrackFormat(index).language }
            }
            .distinct()
    }

    fun onAudioLanguageSelected(language: String) {
        _uiState.value.controller?.let { controller ->
            controller.trackSelectionParameters = controller.trackSelectionParameters
                .buildUpon()
                .setPreferredAudioLanguage(language)
                .build()
        }
    }

}

sealed class PlayerState {
    data object PlayerBuffering: PlayerState()
    data object PlayerReady: PlayerState()
    data object PlayerEnded: PlayerState()
    data object PlayerIdle: PlayerState()
    data object PlayerPlaying: PlayerState()
    data object PlayerPaused: PlayerState()
    data class PlayerError(val exception: PlaybackException) : PlayerState()
    data class PlayerSuppressed(val reason: Int) : PlayerState()
}

data class PlayerUiState(
    val controller: MediaController? = null,
    val playerState: PlayerState = PlayerState.PlayerIdle,
    val subtitleLanguages: List<String> = emptyList(),
    val audioLanguages: List<String> = emptyList(),
    val isServiceRunning: Boolean = false,
    val captionsEnabled: Boolean = false
)