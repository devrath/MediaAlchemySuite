package com.istudio.player.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

const val APP_TAG = "Player-LOGS"

@HiltAndroidApp
class PlayerApplication : Application()