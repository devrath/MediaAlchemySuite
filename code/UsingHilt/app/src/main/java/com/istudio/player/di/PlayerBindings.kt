package com.istudio.player.di

import com.istudio.player.controllers.VideoPlayerControllerImpl
import com.istudio.player.controllers.VideoPlayerPlaybackController
import com.istudio.player.controllers.VideoPlayerSessionController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PlayerBindings {

    @Binds
    fun bindSessionController(impl: VideoPlayerControllerImpl): VideoPlayerSessionController

    @Binds
    fun bindPlaybackController(impl: VideoPlayerControllerImpl): VideoPlayerPlaybackController
}