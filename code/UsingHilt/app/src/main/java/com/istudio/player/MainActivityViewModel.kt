package com.istudio.player

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaController
import com.istudio.player.application.APP_TAG
import com.istudio.player.controllers.VideoPlaybackController
import com.istudio.player.controllers.VideoMediaController
import com.istudio.player.service.PlayerMediaSessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionController: VideoMediaController,
    private val playbackController: VideoPlaybackController
) : ViewModel() {

    private val _controllerState = mutableStateOf<MediaController?>(null)
    val controllerState: State<MediaController?> = _controllerState

    override fun onCleared() {
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

    fun startMediaService(
        context: Context,
        videoUrl: String,
        artworkUrl: String? = null,
        title: String = "Video Player",
        artist: String = "Media Player"
    ) {
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
            Log.d(APP_TAG, "Media service started successfully")
        } catch (e: Exception) {
            Log.e(APP_TAG, "Media service failed to start", e)
        }
    }
}