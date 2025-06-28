package com.istudio.player.controllers

import androidx.media3.session.MediaController
import javax.inject.Inject

class VideoPlayerControllerImpl @Inject constructor(
    private val controllerManager: MediaBinderControllerImpl
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

