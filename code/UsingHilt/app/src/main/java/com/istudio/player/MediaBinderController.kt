package com.istudio.player

import androidx.media3.session.MediaController

interface MediaBinderController {
    suspend fun bindToService(): MediaController
}