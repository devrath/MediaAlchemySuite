package com.istudio.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.istudio.player.service.PlayerMediaSessionService
import com.istudio.player.ui.theme.PlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.media3.session.MediaController
import kotlinx.coroutines.guava.await



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

    val context = LocalContext.current
    val controller by viewModel.controllerState
    val dynamicVideoUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"

    // Example artwork URL - replace with your actual image URL
    val artworkUrl = "https://picsum.photos/512/512" // Random image for demo
    // Or use a local drawable: null (will use default)

    LaunchedEffect(Unit) {
        try {
            // Start the service with artwork information
            viewModel.startMediaService(
                context = context,
                videoUrl = dynamicVideoUrl,
                artworkUrl = artworkUrl,
                title = "Sample Video",
                artist = "Learning Container"
            )

            // Add a small delay to ensure service is started
            kotlinx.coroutines.delay(500)

            viewModel.initializeController()
            viewModel.playVideo()
        } catch (e: Exception) {
            //Log.e("MainScreen", "Error initializing media player", e)
        }
    }

    controller?.let { ctrl ->
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = ctrl
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    useController = true
                }
            },
            modifier = modifier.fillMaxWidth()
                .height(500.dp)
        )
    }
}