package com.istudio.media3.demos.audio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun AudioDemoScreen () {

    val viewModel = koinViewModel<AudioDemoVm>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTrackState by viewModel.currentPlayingIndex.collectAsStateWithLifecycle()
    val isPlayingState by viewModel.isPlaying.collectAsStateWithLifecycle()
    val totalDurationState by viewModel.totalDurationInMS.collectAsStateWithLifecycle()
    var currentPositionState by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isPlayingState) {
        while (isPlayingState) {
            currentPositionState = viewModel.player.currentPosition
            delay(1.seconds)
        }
    }

    when (uiState) {
        PlayerUIState.Loading -> {

        }

        is PlayerUIState.Tracks -> {
            Column(modifier = Modifier.fillMaxSize()) {
                AudioPlayerView(viewModel)
                PlayerControlsView(
                    currentTrackImage = (uiState as PlayerUIState.Tracks).items[currentTrackState].teaserUrl,
                    totalDuration = totalDurationState,
                    currentPosition = currentPositionState,
                    isPlaying = isPlayingState,
                    navigateTrack = { action -> viewModel.updatePlaylist(action) },
                    seekPosition = { position -> viewModel.updatePlayerPosition((position * 1000).toLong()) }
                )
                PlaylistView((uiState as PlayerUIState.Tracks).items, currentTrackState)
            }
        }
    }
}
