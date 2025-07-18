package com.istudio.player.di

import com.istudio.player.controllers.VideoControllerImpl
import com.istudio.player.controllers.VideoPlaybackController
import com.istudio.player.controllers.VideoMediaController
import com.istudio.player.notification.NotificationProviderContract
import com.istudio.player.notification.NotificationProviderContractImpl
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