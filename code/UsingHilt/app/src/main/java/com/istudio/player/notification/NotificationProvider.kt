package com.istudio.player.notification

import android.app.Application
import android.app.Notification
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import com.istudio.player.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationProvider @Inject constructor(
    private val application: Application
) {

    companion object {
        const val PLAYBACK_CHANNEL_ID = "media_playback_channel"
        const val NOTIFICATION_ID = 101
    }

    @OptIn(UnstableApi::class)
    fun createPlaybackChannel() {
        NotificationUtil.createNotificationChannel(
            application,
            PLAYBACK_CHANNEL_ID,
            R.string.media_playback_channel_name,
            R.string.media_playback_channel_description,
            NotificationUtil.IMPORTANCE_LOW
        )
    }

    fun buildInitialNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, PLAYBACK_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Preparing to play...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @OptIn(UnstableApi::class)
    fun createMediaNotificationProvider(context: Context): MediaNotification.Provider {
        return DefaultMediaNotificationProvider.Builder(context)
            .setChannelId(PLAYBACK_CHANNEL_ID)
            .setChannelName(R.string.media_playback_channel_name)
            .setNotificationId(NOTIFICATION_ID)
            .build()
    }
}