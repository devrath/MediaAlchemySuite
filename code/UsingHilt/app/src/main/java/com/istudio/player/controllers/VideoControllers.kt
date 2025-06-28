package com.istudio.player.controllers

import androidx.media3.session.MediaController

interface MediaBinderController {
    suspend fun bindToService(): MediaController
}

interface VideoPlayerPlaybackController {
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
}

interface VideoPlayerSessionController {
    suspend fun initialize(): MediaController
    fun getController(): MediaController?
    fun release()
}