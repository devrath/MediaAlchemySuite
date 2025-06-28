package com.istudio.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.istudio.player.service.PlayerMediaSessionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.guava.await
import javax.inject.Inject

class MediaControllerManager @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaBinderController {

    override suspend fun bindToService(): MediaController {
        val sessionToken = SessionToken(context, ComponentName(context, PlayerMediaSessionService::class.java))
        return MediaController.Builder(context, sessionToken).buildAsync().await()
    }
}