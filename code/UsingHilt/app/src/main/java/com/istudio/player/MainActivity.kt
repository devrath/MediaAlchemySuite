package com.istudio.player

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import com.istudio.player.application.APP_TAG
import com.istudio.player.ui.theme.PlayerTheme
import com.istudio.player.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


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
    // Start service and initiate playback
    StartServiceInitiatePlay(viewModel)
    // Show PlayerView only when the controller is ready
    DisplayPlayer(controller, modifier)
}

@Composable
private fun DisplayPlayer(
    controller: MediaController?,
    modifier: Modifier
) {
    val context = LocalContext.current
    val playerHeight = 500.dp
    if (controller != null) {
        val playerView = remember(controller) {
            preparePlayerView(context, controller)
        }
        AndroidView(
            factory = { playerView },
            modifier = modifier.fillMaxWidth().height(playerHeight)
        )
    }
}

@Composable
private fun StartServiceInitiatePlay(viewModel: MainActivityViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        try {
            // Step 1: Start service
            viewModel.startMediaService(context)
            // Step 2: Initialise media controller
            viewModel.initializeController()
            // Step 3: Play video
            viewModel.playVideo()
            Log.d(APP_TAG, "Playback started successfully")
        } catch (e: Exception) {
            Log.e(APP_TAG, "Error while starting playback", e)
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