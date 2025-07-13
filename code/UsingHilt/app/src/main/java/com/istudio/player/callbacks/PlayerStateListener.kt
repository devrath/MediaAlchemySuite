package com.istudio.player.callbacks

import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import androidx.media3.session.MediaController
import com.istudio.player.application.APP_TAG
import com.istudio.player.ui.screens.PlayerState

class PlayerStateListener(
    private val controller: MediaController,
    private val onPlayerStateChanged: (PlayerState) -> Unit
): Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING, Player.STATE_IDLE -> onPlayerStateChanged(PlayerState.PlayerBuffering)
            Player.STATE_READY -> onPlayerStateChanged(PlayerState.PlayerReady)
            Player.STATE_ENDED -> onPlayerStateChanged(PlayerState.PlayerEnded)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d(APP_TAG, "isPlaying: $isPlaying")
        val suppressionReason = controller.playbackSuppressionReason
        val error = controller.playerError

        when {
            error != null -> {
                Log.e(APP_TAG, "Player error occurred: ${error.message}")
                onPlayerStateChanged(PlayerState.PlayerError(error))
            }

            suppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                Log.w(APP_TAG, "Playback suppressed. Reason: $suppressionReason")
                onPlayerStateChanged(PlayerState.PlayerSuppressed(suppressionReason))
            }

            !controller.playWhenReady -> {
                Log.d(APP_TAG, "Player is paused or waiting.")
                onPlayerStateChanged(PlayerState.PlayerPaused)
            }

            isPlaying -> {
                Log.d(APP_TAG, "Playback is active.")
                onPlayerStateChanged(PlayerState.PlayerPlaying)
            }

            else -> {
                Log.d(APP_TAG, "Unknown playback state fallback.")
                onPlayerStateChanged(PlayerState.PlayerBuffering)
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        when (val cause = error.cause) {
            is HttpDataSource.HttpDataSourceException -> {
                when (cause) {
                    is HttpDataSource.InvalidResponseCodeException -> {
                        Log.e(APP_TAG, "HTTP error code: ${cause.responseCode}")
                    }
                    else -> Log.e(APP_TAG, "HTTP error: ${cause.message}")
                }
            }
            else -> Log.e(APP_TAG, "Playback error: ${error.message}")
        }
        onPlayerStateChanged(PlayerState.PlayerError(error))
    }

    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
            Log.e(APP_TAG, "OnEvents: EVENT_PLAYBACK_STATE_CHANGED")
        }
    }
}