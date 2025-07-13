package com.istudio.player.ui.screens.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.istudio.player.ui.screens.CONTAINER_HEIGHT

@Composable
fun PlayerSuppressed(modifier: Modifier = Modifier, reason: Int, onRetry: () -> Unit) {
    val reasonText = when (reason) {
        Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS -> "Audio focus lost temporarily"
        Player.PLAYBACK_SUPPRESSION_REASON_UNSUITABLE_AUDIO_OUTPUT -> "Unsuitable audio output"
        else -> "Playback suppressed"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CONTAINER_HEIGHT)
            .background(Color.Yellow),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Suppressed", color = Color.Black)
            Text(reasonText, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
            Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                Text("Retry")
            }
        }
    }
}