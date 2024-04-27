package com.istudio.media3.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.istudio.media3.demos.audio.AudioDemoScreen
import com.istudio.media3.main.selection.DemoSelectionScreen
import com.istudio.media3.main.selection.SelectionScreenViewModel
import com.istudio.media3.ui.LocalNavigationProvider
import com.istudio.media3.ui.NavigationRoutes
import com.istudio.media3.ui.theme.Media3Theme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val vm: SelectionScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Media3Theme {
                val navController = LocalNavigationProvider.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavigationRoutes.screenSelection
                    ) {
                        composable(NavigationRoutes.screenSelection) { DemoSelectionScreen() }
                        composable(NavigationRoutes.screenAudioDemo) { AudioDemoScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Media3Theme {
        Greeting("Android")
    }
}