package com.istudio.player

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaController
import com.istudio.player.controller.VideoPlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import com.istudio.player.service.PlayerMediaSessionService
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val videoPlayerController: VideoPlayerController
) : ViewModel() {

    // We place the state here because controller takes time to display and reactively notified of state changes
    private val _controllerState = mutableStateOf<MediaController?>(null)
    val controllerState: State<MediaController?> = _controllerState

    fun startMediaService(context: Context, videoUrl: String) {
        val intent = Intent(context, PlayerMediaSessionService::class.java).apply {
            putExtra(PlayerMediaSessionService.EXTRA_VIDEO_URL, videoUrl)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    suspend fun initializeController() {
        val controller = videoPlayerController.initialize()
        _controllerState.value = controller
    }

    fun playVideo() = videoPlayerController.play()

    override fun onCleared() {
        videoPlayerController.pause()
        super.onCleared()
    }
}