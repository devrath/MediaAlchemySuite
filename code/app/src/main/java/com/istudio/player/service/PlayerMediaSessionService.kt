package com.istudio.player.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.istudio.player.analytics.PlayerAnalyticsListener
import com.istudio.player.analytics.createPlaybackStatsListener
import com.istudio.player.di.qualifiers.MainActivityClass
import com.istudio.player.notification.NotificationProviderContract
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class PlayerMediaSessionService : MediaSessionService() {

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var notificationProviderContract: NotificationProviderContract
    @Inject lateinit var mediaSession: MediaSession

    private var hasInitialized = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!hasInitialized) {
            hasInitialized = true
            initializeSession(intent)
            setAnalytics()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setAnalytics() {
        val analyticsListener = PlayerAnalyticsListener()
        val statsListener = createPlaybackStatsListener()

        exoPlayer.addAnalyticsListener(analyticsListener)
        exoPlayer.addAnalyticsListener(statsListener)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        mediaSession.apply {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
        exoPlayer.release()
        super.onDestroy()
    }

    private fun initializeSession(intent: Intent?) {
        val controls = notificationProviderContract.provideCustomCommandLayout()
        val mediaNotificationProvider = notificationProviderContract.createMediaNotificationProvider(this)
        mediaSession.setCustomLayout(controls)
        setMediaNotificationProvider(mediaNotificationProvider)
    }
}