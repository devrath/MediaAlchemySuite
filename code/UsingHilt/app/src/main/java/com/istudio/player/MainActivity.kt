package com.istudio.player

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
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
            LoadingIndicator(modifier)
        }
        PlayerState.PlayerEnded -> {
            EndedUI(modifier) {
                viewModel.startNewMedia()
            }
        }
        PlayerState.PlayerIdle -> {
            IdleUI(modifier) {
                viewModel.startNewMedia()
            }
        }
        PlayerState.PlayerReady -> {
            DisplayPlayer(controller, modifier) {
                viewModel.startNewMedia()
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
private fun DisplayPlayer(
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
private fun LoadingIndicator(modifier: Modifier = Modifier) {
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
