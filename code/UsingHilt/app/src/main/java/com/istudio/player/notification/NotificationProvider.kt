package com.istudio.player.notification

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import com.istudio.player.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationProvider @Inject constructor(
    private val application: Application
) {

    companion object {
        const val PLAYBACK_CHANNEL_ID = "media_playback_channel"
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
}