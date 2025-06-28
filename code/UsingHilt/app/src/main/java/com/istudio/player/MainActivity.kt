package com.istudio.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
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

    var controller by remember { mutableStateOf<MediaController?>(null) }
    val dynamicVideoUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"

    LaunchedEffect(Unit) {
        viewModel.startMediaService(context, dynamicVideoUrl)

        val sessionToken = SessionToken(context, ComponentName(context, PlayerMediaSessionService::class.java))
        controller = MediaController.Builder(context, sessionToken).buildAsync().await()
        controller?.play()
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
            modifier = modifier
        )
    }
}