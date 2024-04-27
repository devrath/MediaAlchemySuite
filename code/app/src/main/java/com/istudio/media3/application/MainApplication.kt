package com.istudio.media3.application

import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel
import com.istudio.media3.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.viewModel
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(viewModelModules)
        }
    }


}

