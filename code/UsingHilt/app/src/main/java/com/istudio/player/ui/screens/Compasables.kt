package com.istudio.player.ui.screens

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView

@Composable
fun ErrorUI(modifier: Modifier = Modifier, message: String, onRetry: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CONTAINER_HEIGHT)
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Playback Error", color = Color.White)
            Text(message, color = Color.White, modifier = Modifier.padding(top = 4.dp))
            Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun SuppressedUI(modifier: Modifier = Modifier, reason: Int, onRetry: () -> Unit) {
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


@Composable
fun EndedUI(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CONTAINER_HEIGHT)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Playback Ended", color = Color.White)
            Button(onClick = onClick, modifier = Modifier.padding(top = 8.dp)) {
                Text("Play Again")
            }
        }
    }
}


@Composable
fun IdleUI(modifier: Modifier = Modifier, onClick: () -> Unit) {
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

private fun preparePlayerView(
    context: Context,
    controller: MediaController?
): PlayerView = PlayerView(context).apply {
    player = controller
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    useController = true
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CONTAINER_HEIGHT)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}




@Composable
fun DisplayPlayer(
    controller: MediaController?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember(controller) { mutableStateOf(controller?.isPlaying == true) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var captionsEnabled by remember { mutableStateOf(true) }

    if (controller != null) {
        val playerView = remember(controller) {
            preparePlayerView(context, controller)
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AndroidView(factory = { playerView }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    controller.seekBack()
                }) {
                    Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s")
                }

                IconButton(onClick = {
                    isPlaying = if (controller.isPlaying) {
                        controller.pause()
                        false
                    } else {
                        controller.play()
                        true
                    }
                }) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }

                IconButton(onClick = {
                    controller.seekForward()
                }) {
                    Icon(Icons.Default.Forward10, contentDescription = "Forward 10s")
                }

                IconButton(onClick = {
                    captionsEnabled = !captionsEnabled
                    // Toggle captions
                    controller.trackSelectionParameters = controller.trackSelectionParameters
                        .buildUpon()
                        .setPreferredTextLanguage("en")
                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !captionsEnabled)
                        .build()
                }) {
                    Icon(Icons.Default.Subtitles, contentDescription = "Toggle Captions")
                }

                IconButton(onClick = {
                    onClick()
                }) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen")
                }

                IconButton(onClick = {
                    showSpeedDialog = true
                }) {
                    Icon(Icons.Default.Speed, contentDescription = "Playback Speed")
                }
            }
        }

        if (showSpeedDialog) {
            AlertDialog(
                onDismissRequest = { showSpeedDialog = false },
                title = { Text("Select Playback Speed") },
                text = {
                    Column {
                        listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            Text(
                                text = "${speed}x",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        controller.setPlaybackSpeed(speed)
                                        playbackSpeed = speed
                                        showSpeedDialog = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }
    }
}

