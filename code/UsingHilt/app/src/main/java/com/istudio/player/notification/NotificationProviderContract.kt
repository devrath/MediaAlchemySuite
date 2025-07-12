package com.istudio.player.notification

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification

interface NotificationProviderContract {
    fun createPlaybackChannel()

    @OptIn(UnstableApi::class)
    fun createMediaNotificationProvider(context: Context): MediaNotification.Provider
    fun provideCustomCommandLayout(): List<CommandButton>
}