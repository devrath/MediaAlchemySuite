package com.istudio.player.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.istudio.player.MainActivity
import com.istudio.player.callbacks.PlayerMediaSessionCallback
import com.istudio.player.notification.NotificationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerMediaSessionService : MediaSessionService() {

    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
    }

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var notificationProvider: NotificationProvider

    private lateinit var mediaSession: MediaSession
    private var hasInitialized = false

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!hasInitialized) {
            hasInitialized = true
            initializeSession(intent)
        }
        // ✅ Call the superclass method
        return super.onStartCommand(intent, flags, startId)
    }

    @UnstableApi
    private fun initializeSession(intent: Intent?) {
        notificationProvider.createPlaybackChannel()
        startForeground(
            NotificationProvider.NOTIFICATION_ID,
            notificationProvider.buildInitialNotification(this)
        )

        val videoUrl = intent?.getStringExtra(EXTRA_VIDEO_URL)
            ?: throw IllegalArgumentException("Video URL is required")

        exoPlayer.apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }

        val sessionIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionIntent)
            .setCallback(PlayerMediaSessionCallback(exoPlayer))
            .build()

        mediaSession.setCustomLayout(notificationProvider.provideCustomCommandLayout())
        setMediaNotificationProvider(notificationProvider.createMediaNotificationProvider(this))
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        mediaSession.release()
        exoPlayer.release()
        super.onDestroy()
    }

    @UnstableApi
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Pause playback
        exoPlayer.playWhenReady = false
        exoPlayer.pause()

    }
}
