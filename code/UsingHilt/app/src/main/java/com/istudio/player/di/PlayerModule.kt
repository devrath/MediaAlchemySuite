package com.istudio.player.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.istudio.player.MediaControllerManager
import com.istudio.player.controller.VideoPlayerControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideMediaControllerManager(
        @ApplicationContext context: Context
    ): MediaControllerManager = MediaControllerManager(context)

}