package com.istudio.player.controller

import androidx.media3.session.MediaController

interface VideoPlayerController {
    suspend fun initialize(): MediaController
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
    fun getPlayer(): MediaController?
    fun release()
}