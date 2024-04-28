package com.istudio.media3.demos.audio

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AudioDemoScreen () {

    val vm: AudioDemoVm = viewModel()


    Text(text = "Hello")
}
