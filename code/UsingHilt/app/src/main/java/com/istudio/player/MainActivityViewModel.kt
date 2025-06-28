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

    fun startMediaService(context: Context, videoUrl: String) {
        val intent = Intent(context, PlayerMediaSessionService::class.java).apply {
            putExtra(PlayerMediaSessionService.EXTRA_VIDEO_URL, videoUrl)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    suspend fun initializeController() {
        _controllerState.value = sessionController.initialize()
    }

    fun playVideo() = playbackController.play()
}