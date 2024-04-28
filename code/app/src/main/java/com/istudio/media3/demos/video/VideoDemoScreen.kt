package com.istudio.media3.demos.video

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VideoDemoScreen () {

    val vm: VideoDemoVm = viewModel()


    Text(text = "Hello")
}
