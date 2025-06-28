package com.istudio.player.callbacks

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.*
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@UnstableApi
class PlayerMediaSessionCallback(
    private val player: Player
): MediaSession.Callback {

    companion object {
        const val CUSTOM_REWIND = "custom_rewind_10"
        const val CUSTOM_FORWARD = "custom_forward_10"
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val sessionCommands = SessionCommands.Builder()
            .add(SessionCommand(CUSTOM_REWIND, Bundle()))
            .add(SessionCommand(CUSTOM_FORWARD, Bundle()))
            .build()

        val playerCommands = Player.Commands.Builder()
            .addAll(player.availableCommands)
            .build()

        return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .setAvailablePlayerCommands(playerCommands)
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            CUSTOM_REWIND -> {
                val newPosition = (player.currentPosition - 10_000).coerceAtLeast(0)
                player.seekTo(newPosition)
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }

            CUSTOM_FORWARD -> {
                val duration = player.duration.takeIf { it != C.TIME_UNSET } ?: Long.MAX_VALUE
                val newPosition = (player.currentPosition + 10_000).coerceAtMost(duration)
                player.seekTo(newPosition)
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }

            else -> return super.onCustomCommand(session, controller, customCommand, args)
        }
    }
}

