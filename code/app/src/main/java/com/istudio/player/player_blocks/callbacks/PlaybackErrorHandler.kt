package com.istudio.player.player_blocks.callbacks

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackErrorHandler @Inject constructor() {
    var onMaxRetryReached: (() -> Unit)? = null
    var noConnectivity: (() -> Unit)? = null
}
