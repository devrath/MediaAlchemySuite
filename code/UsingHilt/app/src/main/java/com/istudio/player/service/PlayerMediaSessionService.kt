package com.istudio.player.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import com.istudio.player.MainActivity
import com.istudio.player.R
import com.istudio.player.callbacks.PlayerMediaSessionCallback
import com.istudio.player.notification.NotificationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerMediaSessionService : MediaSessionService() {

    companion object {
        const val VIDEO_URL = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
    }

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var notificationProvider: NotificationProvider

    private lateinit var mediaSession: MediaSession

    @UnstableApi
    override fun onCreate() {
        super.onCreate()

        // Create channel and initial notification
        notificationProvider.createPlaybackChannel()
        // Start foreground notification
        startForeground(NotificationProvider.NOTIFICATION_ID, notificationProvider.buildInitialNotification(this))

        // Setup media item and start playback
        exoPlayer.apply {
            val mediaItem = MediaItem.fromUri(VIDEO_URL)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        // Create a session with a session activity that it has to be tied to
        val pendingIntent = getPendingIntent()

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(pendingIntent)
            .setCallback(PlayerMediaSessionCallback(exoPlayer))
            .build()

        mediaSession.setCustomLayout(notificationProvider.provideCustomCommandLayout())


        // Attach media notification provider from NotificationProvider
        setMediaNotificationProvider(notificationProvider.createMediaNotificationProvider(this))
    }

    private fun getPendingIntent(): PendingIntent = PendingIntent.getActivity(
        this, 0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.release()
        exoPlayer.release()
        super.onDestroy()
    }
}