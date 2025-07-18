package com.istudio.player.analytics

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import com.istudio.player.application.APP_TAG

@OptIn(UnstableApi::class)
fun createPlaybackStatsListener(): PlaybackStatsListener {
    return PlaybackStatsListener(/* keepHistory = */ true) { eventTime, playbackStats ->

        val mediaTag = eventTime.timeline
            .getWindow(eventTime.windowIndex, Timeline.Window())
            .mediaItem
            .localConfiguration
            ?.tag
            ?.toString()

        Log.d(
            APP_TAG,
            "Playback completed for [$mediaTag] -- totalPlayTime: ${playbackStats.totalPlayTimeMs}ms, " +
                    "rebuffers: ${playbackStats.totalRebufferCount}, avg bitrate: ${playbackStats.meanVideoFormatBitrate}"
        )
    }
}