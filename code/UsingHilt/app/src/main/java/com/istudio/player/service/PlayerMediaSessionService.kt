package com.istudio.player.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.istudio.player.MainActivity
import com.istudio.player.application.APP_TAG
import com.istudio.player.callbacks.PlayerMediaSessionCallback
import com.istudio.player.di.qualifiers.MainActivityClass
import com.istudio.player.notification.NotificationProviderContract
import com.istudio.player.notification.NotificationProviderContractImpl
import com.istudio.player.utils.Constants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class PlayerMediaSessionService : MediaSessionService() {

    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
        const val EXTRA_ARTWORK_URL = "extra_artwork_url"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
    }

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var notificationProviderContract: NotificationProviderContract

    @Inject
    @MainActivityClass
    lateinit var playerMainActionClass: String

    @Inject
    lateinit var mediaSession: MediaSession

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

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onCreate() {
        super.onCreate()
    }

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

    @UnstableApi
    private fun initializeSession(intent: Intent?) {
        // Create a notification channel
        createChannel()
        // Start Foreground service
        startAppForegroundService()
        // Build Media Item
        val mediaItem = buildMediaItem(intent)
        // Prepare ExoPlayer with MediaItem
        prepareExoPlayer(mediaItem)
        // Set layout for media session
        mediaSession.setCustomLayout(notificationProviderContract.provideCustomCommandLayout())
        // Set media notification provider for service
        setMediaNotificationProvider(notificationProviderContract.createMediaNotificationProvider(this))
    }



    @OptIn(UnstableApi::class)
    private fun buildMediaItem(intent: Intent?): MediaItem {
        val videoUrl = intent?.getStringExtra(EXTRA_VIDEO_URL)
        val artworkUrl = intent?.getStringExtra(EXTRA_ARTWORK_URL)
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "Unknown Title"
        val artist = intent?.getStringExtra(EXTRA_ARTIST) ?: "Unknown Artist"

        // Create MediaItem with metadata including artwork
        val mediaItemBuilder = MediaItem.Builder().setUri(videoUrl)

        // Build metadata
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(title)
            .setArtworkUri(artworkUrl?.toUri())
            .setArtist(artist)

        val mediaItem = mediaItemBuilder
            .setMediaMetadata(metadataBuilder.build())
            .build()

        return mediaItem
    }

    private fun prepareExoPlayer(mediaItem: MediaItem) {
        exoPlayer.apply {
            // Set the media item to be played.
            setMediaItem(mediaItem)
            // Prepare the player.
            prepare()
            playWhenReady = true
        }
    }

    private fun startAppForegroundService() {
        startForeground(
            NOTIFICATION_ID,
            notificationProviderContract.buildInitialNotification(this)
        )
    }

    private fun createChannel() {
        notificationProviderContract.createPlaybackChannel()
    }
}