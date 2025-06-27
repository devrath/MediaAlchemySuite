package com.istudio.media3.di

import com.istudio.media3.demos.video.VideoDemoVm
import com.istudio.media3.main.MainViewModel
import com.istudio.media3.main.selection.SelectionScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/*val mediaModules = module {
    single { provideExoPlayer(androidContext()) }
}*/

val viewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { SelectionScreenViewModel() }
    viewModel { VideoDemoVm(exoPlayer = get()) }
}

/**
 * Exoplayer instance
 */
// fun provideExoPlayer(application: Context): ExoPlayer = ExoPlayer.Builder(application).build()


