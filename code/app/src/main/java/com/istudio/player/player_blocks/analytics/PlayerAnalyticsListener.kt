package com.istudio.player.player_blocks.analytics
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import com.istudio.player.application.APP_TAG
import java.io.IOException

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

    override fun onBandwidthEstimate(
        eventTime: AnalyticsListener.EventTime,
        totalLoadTimeMs: Int,
        totalBytesLoaded: Long,
        bitrateEstimate: Long
    ) {
        Log.d(APP_TAG, "Estimated bitrate: $bitrateEstimate bps")
    }

    override fun onDownstreamFormatChanged(
        eventTime: AnalyticsListener.EventTime,
        mediaLoadData: MediaLoadData
    ) {
        Log.d(APP_TAG, "Format changed: bitrate = ${mediaLoadData.trackFormat?.bitrate}")
    }

    override fun onLoadError(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData,
        error: IOException,
        wasCanceled: Boolean
    ) {
        Log.e(APP_TAG, "Load error on segment ${mediaLoadData.dataType}: ${error.message}")
    }

    // Add more analytics callbacks as needed
}