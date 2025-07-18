package com.istudio.player.controllers

import androidx.media3.session.MediaController

interface VideoPlaybackController {
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
}

interface VideoMediaController {
    suspend fun initialize(): MediaController
    fun getController(): MediaController?
    fun release()
}