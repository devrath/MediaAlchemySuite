package com.istudio.player

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val dynamicVideoUrl = Constants.VIDEO_URL
    val artworkUrl = Constants.ART_WORK_URL
    val context = LocalContext.current
    val controller by viewModel.controllerState

    LaunchedEffect(Unit) {
        try {
            // Service not running — start fresh
            viewModel.startMediaService(
                context = context,
                videoUrl = dynamicVideoUrl,
                artworkUrl = artworkUrl,
                title = "Sample Video",
                artist = "Learning Container"
            )
            // Optional delay to let the service settle
            delay(500)
            viewModel.apply {
                initializeController()
                playVideo()
            }
        } catch (e: Exception) {
            Log.e(APP_TAG, "Error while initializing controller", e)
        }
    }

    controller?.let { ctrl ->
        //Observe the controller state and update the UI accordingly because the controller initialises asynchronously
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
            modifier = modifier.fillMaxWidth().height(500.dp)
        )
    }
}