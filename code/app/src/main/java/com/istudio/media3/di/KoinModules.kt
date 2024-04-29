package com.istudio.media3.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.istudio.media3.data.Tracks
import com.istudio.media3.data.TracksImpl
import com.istudio.media3.demos.audio.ui.AudioDemoVm
import com.istudio.media3.main.MainViewModel
import com.istudio.media3.main.selection.SelectionScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mediaModules = module {
    single { provideExoPlayer(androidContext()) }
    single { provideData() }
}

val viewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { SelectionScreenViewModel() }
    viewModel { AudioDemoVm(get(),get()) }
}

/**
 * Exoplayer instance
 */
fun provideExoPlayer(application: Context): ExoPlayer = ExoPlayer.Builder(application).build()

/**
 * Playlist data
 */
fun provideData() : Tracks  = TracksImpl()
