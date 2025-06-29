package com.istudio.player

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaController
import dagger.hilt.android.lifecycle.HiltViewModel
import com.istudio.player.service.PlayerMediaSessionService
import javax.inject.Inject
import androidx.compose.runtime.State
import com.istudio.player.controllers.VideoPlayerPlaybackController
import com.istudio.player.controllers.VideoPlayerSessionController

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionController: VideoPlayerSessionController,
    private val playbackController: VideoPlayerPlaybackController
) : ViewModel() {
    private val _controllerState = mutableStateOf<MediaController?>(null)
    val controllerState: State<MediaController?> = _controllerState

    private var isServiceStarted = false

    fun startMediaService(
        context: Context,
        videoUrl: String,
        artworkUrl: String? = null,
        title: String = "Video Player",
        artist: String = "Media Player"
    ) {
        if (isServiceStarted) {
            //Log.d("MainActivityViewModel", "Service already started")
            return
        }

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
            isServiceStarted = true
            //Log.d("MainActivityViewModel", "Media service started successfully")
        } catch (e: Exception) {
            //Log.e("MainActivityViewModel", "Failed to start media service", e)
        }
    }

    suspend fun initializeController() {
        _controllerState.value = sessionController.initialize()
    }

    fun playVideo() = playbackController.play()

    override fun onCleared() {
        playbackController.pause()
        sessionController.release()
        isServiceStarted = false
        super.onCleared()
    }
}