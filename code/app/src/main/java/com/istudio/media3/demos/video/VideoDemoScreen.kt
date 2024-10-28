package com.istudio.media3.demos.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.istudio.media3.data.DataSamples
import org.koin.androidx.compose.koinViewModel

@Composable
fun VideoDemoScreen (viewModel: VideoDemoVm = koinViewModel()) {

    val player by viewModel.playerState.collectAsState()

    LaunchedEffect(Unit) {
        // Initialize the ExoPlayer only once
        viewModel.initializePlayer()
    }

    DisposableEffect(Unit) {
        // Call this when the composable is destroyed
        onDispose {
            viewModel.savePlayerState()
            viewModel.releasePlayer()
        }
    }

    Column {
        Media3AndroidView(player)
        PlayerControls(player)
    }

}


@Composable
fun Media3AndroidView(player: ExoPlayer?) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            PlayerView(context).apply {
                this.player = player
            }
        },
        update = { playerView ->
            playerView.player = player
        }
    )
}

@Composable
fun PlayerControls(player: ExoPlayer?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { player?.playWhenReady = true }) {
            Text("Play")
        }
        Button(onClick = { player?.playWhenReady = false }) {
            Text("Pause")
        }

        Button(onClick = {
            player?.seekTo(player.currentPosition - 10_000) // Seek backward 10 seconds
        }) {
            Text("Seek -10s")
        }
        Button(onClick = {
            player?.seekTo(player.currentPosition + 10_000) // Seek forward 10 seconds
        }) {
            Text("Seek +10s")
        }
    }
}
