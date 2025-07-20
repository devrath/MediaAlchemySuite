package com.istudio.player.player_blocks.controllers

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