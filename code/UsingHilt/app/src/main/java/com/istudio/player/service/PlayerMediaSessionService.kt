package com.istudio.player.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import androidx.annotation.OptIn
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.istudio.player.MainActivity
import com.istudio.player.callbacks.PlayerMediaSessionCallback
import com.istudio.player.notification.NotificationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.media3.common.util.Log
import com.istudio.player.R
import com.istudio.player.application.APP_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.edit

@AndroidEntryPoint
class PlayerMediaSessionService : MediaSessionService() {

    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
        const val EXTRA_ARTWORK_URL = "extra_artwork_url"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
    }

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var notificationProvider: NotificationProvider

    private lateinit var mediaSession: MediaSession
    private var hasInitialized = false

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle null intent gracefully
        if (intent == null) {
            Log.w(APP_TAG, "Service started with null intent")
            return START_NOT_STICKY
        }

        if (!hasInitialized) {
            hasInitialized = true
            initializeSession(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @UnstableApi
    private fun initializeSession(intent: Intent?) {
        // Create a notification channel
        notificationProvider.createPlaybackChannel()
        startForeground(
            NotificationProvider.NOTIFICATION_ID,
            notificationProvider.buildInitialNotification(this)
        )

        val videoUrl = intent?.getStringExtra(EXTRA_VIDEO_URL)
        if (videoUrl.isNullOrEmpty()) {
            Log.e(APP_TAG, "Video URL is required but not provided")
            stopSelf()
            return
        }

        val artworkUrl = intent.getStringExtra(EXTRA_ARTWORK_URL)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Unknown Title"
        val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "Unknown Artist"

        // Create MediaItem with metadata including artwork
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(videoUrl)

        // Build metadata
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(title)
            .setArtworkUri(artworkUrl?.toUri())
            .setArtist(artist)

        val mediaItem = mediaItemBuilder
            .setMediaMetadata(metadataBuilder.build())
            .build()

        exoPlayer.apply {
            setMediaItem(mediaItem)
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

    override fun onCreate() {
        super.onCreate()
        getSharedPreferences("player_prefs", MODE_PRIVATE)
            .edit { putBoolean("service_running", true) }
    }

    override fun onDestroy() {
        getSharedPreferences("player_prefs", MODE_PRIVATE)
            .edit { putBoolean("service_running", false) }

        if (::mediaSession.isInitialized) {
            mediaSession.release()
        }
        exoPlayer.release()
        super.onDestroy()
    }
}