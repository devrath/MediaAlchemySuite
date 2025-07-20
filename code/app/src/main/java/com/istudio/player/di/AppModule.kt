package com.istudio.player.di

import android.content.Context
import android.os.Build
import com.istudio.player.di.qualifiers.MainActivityClass
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.istudio.player.ui.screens.MainActivity
import com.istudio.player.di.qualifiers.OsVersionCode
import com.istudio.player.player_blocks.network.NetworkMonitor
import com.istudio.player.player_blocks.network.NetworkMonitorImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @OsVersionCode
    fun provideOsVersionCode(): Int = Build.VERSION.SDK_INT

    @Provides
    @MainActivityClass
    fun provideMainActivityClass(): String = MainActivity::class.java.name

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitorImpl(context)

}