package com.istudio.player.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import com.istudio.player.ui.screens.composables.PlayerLoading
import com.istudio.player.ui.screens.composables.PlayerEnded
import com.istudio.player.ui.screens.composables.PlayerIdle
import com.istudio.player.ui.screens.composables.PlayerSuppressed
import com.istudio.player.ui.screens.composables.PlayerError
import com.istudio.player.ui.theme.PlayerTheme
import dagger.hilt.android.AndroidEntryPoint

val CONTAINER_HEIGHT = 500.dp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayerTheme {
                val viewModel: MainActivityViewModel = viewModel()
                val controller by viewModel.controllerState
                val playerState by viewModel.playerState.collectAsState()
                val subtitleLanguages by viewModel.subtitleLanguages.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        playerState = playerState,
                        controller = controller,
                        onPlayPause = { viewModel.onPlayPauseToggle() },
                        onSeekBack = { viewModel.onSeekBack() },
                        onSeekForward = { viewModel.onSeekForward() },
                        onCaptionsToggle = { viewModel.onToggleCaptions() },
                        onSpeedSelected = { viewModel.onPlaybackSpeedSelected(it) },
                        onStartNewMedia = { viewModel.startNewMedia() },
                        availableSubtitles = subtitleLanguages,
                        onSubtitleSelected = { viewModel.onSubtitleLanguageSelected(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    playerState: PlayerState,
    controller: MediaController?,
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onCaptionsToggle: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onStartNewMedia: () -> Unit,
    availableSubtitles: List<String>,
    onSubtitleSelected: (String) -> Unit
) {
    when (playerState) {
        PlayerState.PlayerBuffering -> {
            PlayerLoading(modifier)
        }
        PlayerState.PlayerEnded -> {
            PlayerEnded(modifier) {
                onStartNewMedia()
            }
        }
        PlayerState.PlayerIdle -> {
            PlayerIdle(modifier) {
                onStartNewMedia()
            }
        }
        PlayerState.PlayerReady,
        PlayerState.PlayerPlaying,
        PlayerState.PlayerPaused -> {
            MainScreenComposable(
                controller = controller,
                modifier = modifier,
                onPlayPause = onPlayPause,
                onSeekBack = onSeekBack,
                onSeekForward = onSeekForward,
                onCaptionsToggle = onCaptionsToggle,
                onSpeedSelected = onSpeedSelected,
                fullScreenClick = {},
                availableSubtitles = availableSubtitles,
                onSubtitleSelected = onSubtitleSelected
            )
        }
        is PlayerState.PlayerError -> {
            val errorMessage = playerState.exception.message ?: "Unknown error"
            PlayerError(modifier, errorMessage) {
                onStartNewMedia()
            }
        }
        is PlayerState.PlayerSuppressed -> {
            val reason = playerState.reason
            PlayerSuppressed(modifier, reason) {
                onStartNewMedia()
            }
        }
    }
}