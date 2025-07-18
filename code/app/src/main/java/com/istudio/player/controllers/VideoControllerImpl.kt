package com.istudio.player.controllers

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.istudio.player.application.APP_TAG
import com.istudio.player.service.PlayerMediaSessionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.guava.await
import javax.inject.Inject

class VideoControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): VideoMediaController, VideoPlaybackController {

    private var mediaController: MediaController? = null

    override suspend fun initialize(): MediaController {
        val sessionToken = SessionToken(context, ComponentName(context, PlayerMediaSessionService::class.java))
        Log.d(APP_TAG, "Session token created: --> $sessionToken")
        return MediaController.Builder(context, sessionToken).buildAsync().await()
    }

    override fun getController(): MediaController? = mediaController

    override fun play() { mediaController?.play() }

    override fun pause() { mediaController?.pause() }

    override fun seekTo(positionMs: Long) { mediaController?.seekTo(positionMs) }

    override fun release() {
        mediaController?.release()
        mediaController = null
    }
}

