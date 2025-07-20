package com.istudio.player.di

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import androidx.media3.session.MediaSession
import com.istudio.player.player_blocks.callbacks.PlayerMediaSessionCallback
import com.istudio.player.di.qualifiers.MainActivityClass
import com.istudio.player.player_blocks.callbacks.PlaybackErrorHandler
import com.istudio.player.player_blocks.policies.CustomLoadErrorHandlingPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun providePlaybackErrorHandler(): PlaybackErrorHandler = PlaybackErrorHandler()

    @Provides
    @Singleton
    @UnstableApi
    fun provideLoadErrorPolicy(
        errorHandler: PlaybackErrorHandler
    ): LoadErrorHandlingPolicy {
        // Your custom retry policy for segment errors (e.g., 404)
        return CustomLoadErrorHandlingPolicy(errorHandler)
    }

    @Provides
    @Singleton
    @UnstableApi
    fun provideMediaSourceFactory(
        @ApplicationContext context: Context,
        loadErrorPolicy: LoadErrorHandlingPolicy
    ): DefaultMediaSourceFactory {
        // Set the policy on the MediaSourceFactory
        return DefaultMediaSourceFactory(context)
            .setLoadErrorHandlingPolicy(loadErrorPolicy)
    }

    @Provides
    @Singleton
    @UnstableApi
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        mediaSourceFactory: DefaultMediaSourceFactory
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(context))
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideSessionIntent(
        @ApplicationContext context: Context,
        @MainActivityClass mainActivityClass: String,
    ): PendingIntent {
        /**
         *
         * This method creates a PendingIntent that starts the app module MainActivity.
         * The PendingIntent is created with the "FLAG_UPDATE_CURRENT" flag, which means that if the described PendingIntent already exists,
         * Then keep it, but replace its extra data with what is in this new Intent.
         *
         * @return A PendingIntent that starts the MainActivity. If the PendingIntent cannot be created for any reason, it returns null.
         */
        return PendingIntent.getActivity(
            context, 0,
            Intent(Intent.ACTION_MAIN).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                component = ComponentName(context.packageName, mainActivityClass)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    @Provides
    @Singleton
    @UnstableApi
    fun provideMediaSession(
        @ApplicationContext context: Context,
        sessionIntent: PendingIntent,
        player: ExoPlayer,
    ): MediaSession = MediaSession.Builder(context, player)
        .setSessionActivity(sessionIntent)
        .setCallback(PlayerMediaSessionCallback(player))
        .build()

}