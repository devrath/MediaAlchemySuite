package com.istudio.player

interface VideoPlayerPlaybackController {
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
}