package com.istudio.media3.player

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaModules = module {
    single { provideExoPlayer(androidContext()) }
}

fun provideExoPlayer(application: Context): ExoPlayer = ExoPlayer.Builder(application).build()
