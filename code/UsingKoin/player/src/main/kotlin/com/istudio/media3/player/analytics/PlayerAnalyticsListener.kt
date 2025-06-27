package com.istudio.media3.player.analytics

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import java.io.IOException

@UnstableApi
class MediaAnalyticsListener : AnalyticsListener {

    @OptIn(UnstableApi::class)
    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: PlaybackException) {
        Log.e("MediaAnalyticsListener", "Player error occurred: ${error.message}")
        // Report the error to analytics or log system
    }

    @OptIn(UnstableApi::class)
    override fun onTracksChanged(eventTime: AnalyticsListener.EventTime, tracks: Tracks) {
        Log.d("MediaAnalyticsListener", "Tracks changed, new selections: $tracks.")
        // Track selection or quality change
    }

    @OptIn(UnstableApi::class)
    override fun onRenderedFirstFrame(
        eventTime: AnalyticsListener.EventTime,
        output: Any,
        renderTimeMs: Long
    ) {
        Log.d("MediaAnalyticsListener", "First frame rendered at ${eventTime.realtimeMs}")
        // Log first frame time for analysis of initial buffer times
    }

    override fun onIsPlayingChanged(eventTime: AnalyticsListener.EventTime, isPlaying: Boolean) {
        Log.d("MediaAnalyticsListener", "Is playing state changed: $isPlaying")
        // Track playback state (play/pause)
    }

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> Log.d("MediaAnalyticsListener", "Player buffering")
            Player.STATE_READY -> Log.d("MediaAnalyticsListener", "Player ready to play")
            Player.STATE_ENDED -> Log.d("MediaAnalyticsListener", "Playback ended")
        }
    }

    override fun onBandwidthEstimate(
        eventTime: AnalyticsListener.EventTime,
        totalLoadTimeMs: Int,
        totalBytesLoaded: Long,
        bitrateEstimate: Long
    ) {
        Log.d("MediaAnalyticsListener", "Bandwidth estimate: $bitrateEstimate")
        // Track bandwidth changes, possibly adjust quality
    }

    override fun onLoadError(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData,
        error: IOException,
        wasCanceled: Boolean
    ) {
        Log.e("MediaAnalyticsListener", "Load error: ${error.message}, Canceled: $wasCanceled")
        // Capture network or file load errors for retry strategy
    }

    override fun onAudioAttributesChanged(
        eventTime: AnalyticsListener.EventTime,
        audioAttributes: AudioAttributes
    ) {
        Log.d("MediaAnalyticsListener", "Audio attributes changed: $audioAttributes")
        // Update analytics or settings based on new audio attributes
    }

    override fun onSeekStarted(eventTime: AnalyticsListener.EventTime) {
        Log.d("MediaAnalyticsListener", "Seek started")
        // Track seek start
    }

    override fun onTrackSelectionParametersChanged(
        eventTime: AnalyticsListener.EventTime,
        parameters: TrackSelectionParameters
    ) {
        Log.d("MediaAnalyticsListener", "Track selection parameters changed: $parameters")
        // Adjust tracking based on new track selection parameters
    }

    override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime, reason: Int) {
        Log.d("MediaAnalyticsListener", "Timeline changed: $reason")
        // Track when timeline changes (like next episode)
    }

    override fun onIsLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {
        Log.d("MediaAnalyticsListener", "Loading state changed: $isLoading")
        // Track buffering state
    }

    override fun onVolumeChanged(eventTime: AnalyticsListener.EventTime, volume: Float) {
        Log.d("MediaAnalyticsListener", "Volume changed to $volume")
        // Log volume changes
    }
}