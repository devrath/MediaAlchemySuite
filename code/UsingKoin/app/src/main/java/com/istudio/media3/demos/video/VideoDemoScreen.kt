package com.istudio.media3.demos.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
        PlayerControls(player, playClickAction = {
            player?.let { it.playWhenReady = true }
        }, pauseClickAction = {
            player?.let { it.playWhenReady = false }
        }, forwardAction = {
            player?.let { it.seekTo(it.currentPosition + 10_000) }
        }, rewindAction = {
            player?.let { it.seekTo(it.currentPosition - 10_000) }
        })
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
fun PlayerControls(
    player: ExoPlayer?,
    playClickAction :() -> Unit,
    pauseClickAction :() -> Unit,
    forwardAction :() -> Unit,
    rewindAction :() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { playClickAction() }) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
        }
        IconButton(onClick = { pauseClickAction() }) {
            Icon(Icons.Default.Pause, contentDescription = "Pause")
        }
        IconButton(onClick = { rewindAction() }) {
            Icon(Icons.Default.Replay10, contentDescription = "Seek -10s")
        }
        IconButton(onClick = { forwardAction() }) {
            Icon(Icons.Default.Forward10, contentDescription = "Seek +10s")
        }
    }
}
