package com.istudio.player.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        uiState = uiState,
                        onPlayPause = viewModel::onPlayPauseToggle,
                        onSeekBack = viewModel::onSeekBack,
                        onSeekForward = viewModel::onSeekForward,
                        onCaptionsToggle = viewModel::onToggleCaptions,
                        onSpeedSelected = viewModel::onPlaybackSpeedSelected,
                        onStartNewMedia = viewModel::startNewMedia,
                        onSubtitleSelected = viewModel::onSubtitleLanguageSelected,
                        onAudioSelected = viewModel::onAudioLanguageSelected,
                        onShowSpeedDialog = viewModel::showSpeedDialog,
                        onShowSubtitleDialog = viewModel::showSubtitleDialog,
                        onShowAudioDialog = viewModel::showAudioDialog,
                        onResolutionSelected = viewModel::onResolutionSelected,
                        onShowResolutionDialog = viewModel::showResolutionDialog
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onCaptionsToggle: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onStartNewMedia: () -> Unit,
    onSubtitleSelected: (String) -> Unit,
    onAudioSelected: (String) -> Unit,
    onShowSpeedDialog: (Boolean) -> Unit,
    onShowSubtitleDialog: (Boolean) -> Unit,
    onShowAudioDialog: (Boolean) -> Unit,
    onResolutionSelected: (Int) -> Unit,
    onShowResolutionDialog: (Boolean) -> Unit
) {
    when (val playerState = uiState.playerState) {
        PlayerState.PlayerBuffering -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                PlayerLoading()
            }
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
                controller = uiState.controller,
                modifier = modifier,
                onPlayPause = onPlayPause,
                onSeekBack = onSeekBack,
                onSeekForward = onSeekForward,
                onCaptionsToggle = onCaptionsToggle,
                onSpeedSelected = onSpeedSelected,
                availableSubtitles = uiState.subtitleLanguages,
                onSubtitleSelected = onSubtitleSelected,
                availableAudioLanguages = uiState.audioLanguages,
                onAudioSelected = onAudioSelected,
                isPlaying = uiState.isPlaying,
                showSpeedDialog = uiState.showSpeedDialog,
                showSubtitleDialog = uiState.showSubtitleDialog,
                showAudioDialog = uiState.showAudioDialog,
                showResolutionDialog = uiState.showResolutionDialog,
                availableResolutions = uiState.availableResolutions,
                onResolutionSelected = onResolutionSelected,
                onShowSpeedDialog = onShowSpeedDialog,
                onShowSubtitleDialog = onShowSubtitleDialog,
                onShowAudioDialog = onShowAudioDialog,
                onShowResolutionDialog = onShowResolutionDialog
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