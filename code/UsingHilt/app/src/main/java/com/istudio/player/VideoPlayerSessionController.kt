package com.istudio.player

import androidx.media3.session.MediaController

interface VideoPlayerSessionController {
    suspend fun initialize(): MediaController
    fun getController(): MediaController?
    fun release()
}