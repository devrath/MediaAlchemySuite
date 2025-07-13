package com.istudio.player.ui.screens

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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



@Composable
fun DisplayPlayer(
    controller: MediaController?,
    modifier: Modifier = Modifier,
    onClick: ()-> Unit
) {
    val context = LocalContext.current
    if (controller != null) {
        val playerView = remember(controller) {
            preparePlayerView(context, controller)
        }
        Column {
            AndroidView(
                factory = { playerView },
                modifier = modifier.fillMaxWidth().height(CONTAINER_HEIGHT)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    onClick.invoke()
                }) {
                    Text("PlayItem")
                }
            }
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
