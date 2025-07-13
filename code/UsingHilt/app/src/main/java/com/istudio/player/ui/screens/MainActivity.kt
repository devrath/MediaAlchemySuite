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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel()
) {
    val controller by viewModel.controllerState
    val playerState by viewModel.playerState.collectAsState()

    when(playerState) {
        PlayerState.PlayerBuffering -> {
            PlayerLoading(modifier)
        }
        PlayerState.PlayerEnded -> {
            PlayerEnded(modifier) {
                viewModel.startNewMedia()
            }
        }
        PlayerState.PlayerIdle -> {
            PlayerIdle(modifier) {
                viewModel.startNewMedia()
            }
        }
        PlayerState.PlayerReady,
        PlayerState.PlayerPlaying,
        PlayerState.PlayerPaused -> {
            DisplayPlayer(controller, modifier) {
                viewModel.startNewMedia()
            }
        }
        is PlayerState.PlayerError -> {
            val errorMessage = (playerState as PlayerState.PlayerError).exception.message ?: "Unknown error"
            PlayerError(modifier, errorMessage) {
                viewModel.startNewMedia()
            }
        }
        is PlayerState.PlayerSuppressed -> {
            val reason = (playerState as PlayerState.PlayerSuppressed).reason
            PlayerSuppressed(modifier, reason) {
                viewModel.startNewMedia()
            }
        }
    }
}