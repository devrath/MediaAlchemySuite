package com.istudio.player.ui.screens

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import android.graphics.Color
import android.text.Layout
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.media3.ui.SubtitleView
import com.istudio.player.R
import com.istudio.player.ui.screens.composables.SubtitleSelectionDialog


@Composable
fun MainScreenComposable(
    controller: MediaController?,
    modifier: Modifier = Modifier,
    fullScreenClick: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onCaptionsToggle: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    availableSubtitles: List<String>,
    onSubtitleSelected: (String) -> Unit
) {
    var isPlaying by remember { mutableStateOf(controller?.isPlaying == true) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

            PlayerControlsRow(
                isPlaying = isPlaying,
                onPlayPause = {
                    onPlayPause()
                    isPlaying = !isPlaying
                },
                onSeekBack = onSeekBack,
                onSeekForward = onSeekForward,
                onCaptionsToggle = onCaptionsToggle,
                onFullScreen = fullScreenClick,
                onSpeedClick = { showSpeedDialog = true },
                onSubtitleClick = { showSubtitleDialog = true }
            )

            if (showSpeedDialog) {
                PlaybackSpeedDialog(
                    onDismiss = { showSpeedDialog = false },
                    onSpeedSelected = onSpeedSelected
                )
            }

            if (showSubtitleDialog) {
                SubtitleSelectionDialog(
                    availableLanguages = availableSubtitles,
                    onLanguageSelected = {
                        onSubtitleSelected(it)
                        showSubtitleDialog = false
                    },
                    onDismiss = { showSubtitleDialog = false }
                )
            }
        }
    }
}

@Composable
fun PlaybackSpeedDialog(
    onDismiss: () -> Unit,
    onSpeedSelected: (Float) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Playback Speed") },
        text = {
            Column {
                listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                    Text(
                        text = "${speed}x",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSpeedSelected(speed)
                                onDismiss()
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

@Composable
fun PlayerControlsRow(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onCaptionsToggle: () -> Unit,
    onFullScreen: () -> Unit,
    onSpeedClick: () -> Unit,
    onSubtitleClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSeekBack) {
            Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s")
        }

        IconButton(onClick = onPlayPause) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }

        IconButton(onClick = onSeekForward) {
            Icon(Icons.Default.Forward10, contentDescription = "Forward 10s")
        }

        IconButton(onClick = onFullScreen) {
            Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen")
        }

        IconButton(onClick = onSpeedClick) {
            Icon(Icons.Default.Speed, contentDescription = "Playback Speed")
        }

        IconButton(onClick = onCaptionsToggle) {
            Icon(Icons.Default.ClosedCaption, contentDescription = "Toggle Captions")
        }

        IconButton(onClick = onSubtitleClick) {
            Icon(Icons.Default.Subtitles, contentDescription = "Subtitle Language")
        }
    }
}

@OptIn(UnstableApi::class)
private fun preparePlayerView(
    context: Context,
    controller: MediaController?
): PlayerView = PlayerView(context).apply {
    val dummyParent = FrameLayout(context)
    val playerView = LayoutInflater.from(context).inflate(
        R.layout.player_view_layout,
        dummyParent,
        false
    ) as PlayerView

    playerView.player = controller
    playerView.useController = true

    playerView.subtitleView?.apply {
        setStyle(
            CaptionStyleCompat(
                Color.WHITE,
                Color.BLACK,
                Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                Color.BLACK,
                Typeface.DEFAULT_BOLD
            )
        )
        setFractionalTextSize(0.06f)
        setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setBottomPaddingFraction(0.1f)
        subtitleView?.visibility = View.VISIBLE
        textAlignment = View.TEXT_ALIGNMENT_INHERIT
    }

    return playerView
}

