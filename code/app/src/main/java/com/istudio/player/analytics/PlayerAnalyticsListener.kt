package com.istudio.player.analytics
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.istudio.player.application.APP_TAG

@OptIn(UnstableApi::class)
class PlayerAnalyticsListener: AnalyticsListener {

    override fun onPlaybackStateChanged(
        eventTime: AnalyticsListener.EventTime,
        @Player.State state: Int
    ) {
        val stateName = when (state) {
            Player.STATE_IDLE -> "IDLE"
            Player.STATE_BUFFERING -> "BUFFERING"
            Player.STATE_READY -> "READY"
            Player.STATE_ENDED -> "ENDED"
            else -> "UNKNOWN"
        }
        Log.d(APP_TAG, "onPlaybackStateChanged: $stateName at position ${eventTime.eventPlaybackPositionMs}")
    }

    override fun onDroppedVideoFrames(
        eventTime: AnalyticsListener.EventTime,
        droppedFrames: Int,
        elapsedMs: Long
    ) {
        Log.w(APP_TAG, "Dropped $droppedFrames frames in ${elapsedMs}ms at ${eventTime.eventPlaybackPositionMs}ms")
    }

    // Add more analytics callbacks as needed
}