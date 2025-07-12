@file:OptIn(SavedStateHandleSaveableApi::class)

package com.istudio.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.session.MediaController
import com.istudio.player.application.APP_TAG
import com.istudio.player.controllers.VideoPlaybackController
import com.istudio.player.controllers.VideoMediaController
import com.istudio.player.service.JetAudioState
import com.istudio.player.service.PlayerEvent
import com.istudio.player.service.PlayerMediaSessionService
import com.istudio.player.service.PlayerMediaSessionServiceHandler
import com.istudio.player.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionController: VideoMediaController,
    private val playbackController: VideoPlaybackController,
    private val playerServiceHandler: PlayerMediaSessionServiceHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var isServiceRunning = false

    var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }

    private val _controllerState = mutableStateOf<MediaController?>(null)
    val controllerState: State<MediaController?> = _controllerState

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    is JetAudioState.Initial -> _uiState.value = UIState.Initial
                    is JetAudioState.Buffering -> calculateProgressValue(mediaState.progress)
                    is JetAudioState.Playing -> {
                        isPlaying = mediaState.isPlaying
                    }
                    is JetAudioState.Progress -> calculateProgressValue(mediaState.progress)

                    is JetAudioState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }

                    else -> { }
                }
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            playerServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        pauseVideo()
        stopVideo()
        super.onCleared()
    }

    suspend fun initializeController() {
        _controllerState.value = sessionController.initialize()
    }

    fun playVideo() = playbackController.play()

    private fun pauseVideo() = playbackController.pause()

    private fun stopVideo() = sessionController.release()

    fun startMediaService(context: Context) {
        if (!isServiceRunning) {
            val videoUrl = Constants.VIDEO_URL
            val artworkUrl = Constants.ART_WORK_URL
            val title = "Add video player title here"
            val artist = "Add artist for player here"
            try {
                val intent = Intent(context, PlayerMediaSessionService::class.java).apply {
                    putExtra(PlayerMediaSessionService.EXTRA_VIDEO_URL, videoUrl)
                    artworkUrl?.let {
                        putExtra(PlayerMediaSessionService.EXTRA_ARTWORK_URL, it)
                    }
                    putExtra(PlayerMediaSessionService.EXTRA_TITLE, title)
                    putExtra(PlayerMediaSessionService.EXTRA_ARTIST, artist)
                }
                ContextCompat.startForegroundService(context, intent)
                isServiceRunning = true
                Log.d(APP_TAG, "Media service started successfully")
            } catch (e: Exception) {
                Log.e(APP_TAG, "Media service failed to start", e)
            }
        }
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat() / duration.toFloat()) * 100f)
            else 0f
        progressString = formatDuration(currentProgress)
    }

    @SuppressLint("DefaultLocale")
    private fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minute, seconds)
    }
}

sealed class UIEvents {
    data object PlayPause : UIEvents()
    data class SelectedAudioChange(val index: Int) : UIEvents()
    data class SeekTo(val position: Float) : UIEvents()
    data object SeekToNext : UIEvents()
    data object Backward : UIEvents()
    data object Forward : UIEvents()
    data class UpdateProgress(val newProgress: Float) : UIEvents()
}

sealed class UIState {
    data object Initial : UIState()
    data object Ready : UIState()
}