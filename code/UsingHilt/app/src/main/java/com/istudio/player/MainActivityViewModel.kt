package com.istudio.player

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.istudio.player.service.PlayerMediaSessionService
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    fun startMediaService(context: Context) {
        val intent = Intent(context, PlayerMediaSessionService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun onCleared() {
        super.onCleared()
        // No player logic here
    }
}