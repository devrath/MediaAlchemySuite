package com.istudio.player.controller

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.istudio.player.service.PlayerMediaSessionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoPlayerControllerImpl @Inject constructor(
    private val context: Context
): VideoPlayerController {

    private var mediaController: MediaController? = null

    override suspend fun initialize(): MediaController {
        return withContext(Dispatchers.Main) {
            val sessionToken = SessionToken(context, ComponentName(context, PlayerMediaSessionService::class.java))
            mediaController = MediaController.Builder(context, sessionToken).buildAsync().await()
            mediaController!!
        }
    }

    override fun play() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    override fun getPlayer(): MediaController? = mediaController

    override fun release() {
        mediaController?.release()
        mediaController = null
    }
}