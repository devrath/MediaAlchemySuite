package com.istudio.media3.di

import androidx.media3.exoplayer.ExoPlayer
import com.istudio.media3.demos.audio.ui.AudioDemoVm
import com.istudio.media3.main.MainViewModel
import com.istudio.media3.main.selection.SelectionScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { SelectionScreenViewModel() }
    viewModel { AudioDemoVm(get()) }
}

val mediaModules = module {
    single { ExoPlayer.Builder(get()).build() }
}