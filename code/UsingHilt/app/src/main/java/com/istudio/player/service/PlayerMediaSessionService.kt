package com.istudio.player.service

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.istudio.player.MainActivity
import com.istudio.player.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val NOTIFICATION_ID = 101
const val CHANNEL_ID = "media_playback_channel"

@AndroidEntryPoint
class PlayerMediaSessionService : MediaSessionService() {

    companion object {
        const val VIDEO_URL = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
    }


    @Inject
    lateinit var exoPlayer: ExoPlayer

    private lateinit var mediaSession: MediaSession

    @UnstableApi
    override fun onCreate() {
        super.onCreate()

        val initialNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Preparing to play...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, initialNotification)

        // Prepare MediaItem
        val mediaItem = MediaItem.fromUri(VIDEO_URL)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        // Create media session
        val sessionActivity = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivity)
            .build()

        val notificationProvider = DefaultMediaNotificationProvider.Builder(this)
            .setChannelId(CHANNEL_ID)
            .setChannelName(R.string.media_playback_channel_name)
            .setNotificationId(NOTIFICATION_ID)
            .build()

        setMediaNotificationProvider(notificationProvider)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.release()
        exoPlayer.release()
        super.onDestroy()
    }
}
