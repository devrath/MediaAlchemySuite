package com.istudio.player.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.istudio.player.MainActivity
import com.istudio.player.notification.NotificationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerMediaSessionService : MediaSessionService() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject lateinit var notificationProvider: NotificationProvider

    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()

        // Ensure the channel is created before any notification is shown
        notificationProvider.createPlaybackChannel()

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent)
            //.setCallback(MyMediaSessionCallback())
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
       return mediaSession
    }
}