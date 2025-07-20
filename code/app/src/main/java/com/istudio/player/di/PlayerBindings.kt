package com.istudio.player.di

import com.istudio.player.player_blocks.controllers.VideoControllerImpl
import com.istudio.player.player_blocks.controllers.VideoPlaybackController
import com.istudio.player.player_blocks.controllers.VideoMediaController
import com.istudio.player.player_blocks.notification.NotificationProviderContract
import com.istudio.player.player_blocks.notification.NotificationProviderContractImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PlayerBindings {

    @Binds
    fun bindSessionController(impl: VideoControllerImpl): VideoMediaController

    @Binds
    fun bindPlaybackController(impl: VideoControllerImpl): VideoPlaybackController

    @Binds
    fun bindNotificationContract(impl: NotificationProviderContractImpl): NotificationProviderContract

}