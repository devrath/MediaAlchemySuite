package com.istudio.player.ui.screens.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.istudio.player.ui.screens.CONTAINER_HEIGHT

@Composable
fun PlayerIdle(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CONTAINER_HEIGHT)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onClick) {
            Text("Start Playback")
        }
    }
}