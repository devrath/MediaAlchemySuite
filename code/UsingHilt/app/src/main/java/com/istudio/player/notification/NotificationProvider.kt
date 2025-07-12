package com.istudio.player.notification

import android.app.Application
import android.app.Notification
import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.SessionCommand
import com.istudio.player.R
import com.istudio.player.callbacks.PlayerMediaSessionCallback
import com.istudio.player.utils.Constants.CHANNEL_NAME
import com.istudio.player.utils.Constants.NOTIFICATION_ID
import com.istudio.player.utils.Constants.PLAYBACK_CHANNEL_ID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationProvider @Inject constructor(
    private val application: Application
) {
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
            .setChannelName(CHANNEL_NAME)
            .setNotificationId(NOTIFICATION_ID)
            .build()
    }

    @OptIn(UnstableApi::class)
    fun provideCustomCommandLayout(): List<CommandButton> {
        val rewindCommand = SessionCommand(PlayerMediaSessionCallback.CUSTOM_REWIND, Bundle())
        val forwardCommand = SessionCommand(PlayerMediaSessionCallback.CUSTOM_FORWARD, Bundle())

        return listOf(
            CommandButton.Builder(CommandButton.ICON_UNDEFINED)
                .setIconResId(R.drawable.replay_10)
                .setDisplayName("Rewind")
                .setSessionCommand(rewindCommand)
                .build(),

            // Do the same for the forward button.
            CommandButton.Builder(CommandButton.ICON_UNDEFINED)
                .setIconResId(R.drawable.forward_10)
                .setDisplayName("Forward")
                .setSessionCommand(forwardCommand)
                .build()
        )
    }

}