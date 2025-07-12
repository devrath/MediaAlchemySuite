package com.istudio.player.di

import android.os.Build
import com.istudio.player.di.qualifiers.MainActivityClass
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.istudio.player.MainActivity
import com.istudio.player.di.qualifiers.OsVersionCode
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

}