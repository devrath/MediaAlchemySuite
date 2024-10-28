package com.istudio.media3.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.istudio.media3.demos.audio.permission.RequestNotificationPermissions
import com.istudio.media3.demos.audio.ui.AudioDemoScreen
import com.istudio.media3.demos.video.VideoDemoScreen
import com.istudio.media3.main.selection.DemoSelectionScreen
import com.istudio.media3.main.selection.SelectionScreenViewModel
import com.istudio.media3.ui.LocalNavigationProvider
import com.istudio.media3.ui.NavigationRoutes
import com.istudio.media3.ui.theme.Media3Theme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val vm: SelectionScreenViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Media3Theme { MainScreen() } }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = LocalNavigationProvider.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        RequestNotificationPermissions()
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.screenSelection
        ) {
            composable(NavigationRoutes.screenSelection) { DemoSelectionScreen() }
            composable(NavigationRoutes.screenAudioDemo) { AudioDemoScreen() }
            composable(NavigationRoutes.screenPlayVideoFromLocal) { VideoDemoScreen() }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Media3Theme {
        MainScreen()
    }
}