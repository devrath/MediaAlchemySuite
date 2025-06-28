package com.istudio.player.controller

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.istudio.player.MediaControllerManager
import com.istudio.player.VideoPlayerPlaybackController
import com.istudio.player.VideoPlayerSessionController
import com.istudio.player.service.PlayerMediaSessionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoPlayerControllerImpl @Inject constructor(
    private val controllerManager: MediaControllerManager
): VideoPlayerSessionController, VideoPlayerPlaybackController {

    private var mediaController: MediaController? = null

    override suspend fun initialize(): MediaController {
        mediaController = controllerManager.bindToService()
        return mediaController!!
    }

    override fun getController(): MediaController? = mediaController
    override fun play() {
        mediaController?.play()
    }
    override fun pause() {
        mediaController?.pause()
    }
    override fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }
    override fun release() {
        mediaController?.release()
        mediaController = null
    }
}

