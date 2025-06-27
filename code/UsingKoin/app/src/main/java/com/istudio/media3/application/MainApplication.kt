package com.istudio.media3.application

import android.app.Application
import com.istudio.media3.di.viewModelModules
import com.istudio.media3.player.mediaModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(mediaModules, viewModelModules)
        }

    }


}

