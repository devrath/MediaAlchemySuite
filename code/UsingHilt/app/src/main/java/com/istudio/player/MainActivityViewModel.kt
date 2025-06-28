package com.istudio.player

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val player: ExoPlayer
) : ViewModel() {

    companion object {
        const val VIDEO_URL = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
    }

    fun getPlayer(): ExoPlayer = player


    fun playMedia() {
        // Build the media item.
        val mediaItem = MediaItem.fromUri(VIDEO_URL)
        player.apply {
            setMediaItem(mediaItem)
            addListener(setPlayerListener())
            prepare()
            play()
        }
    }

    private fun setPlayerListener() = object: Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            val cause = error.cause
            if (cause is HttpDataSource.HttpDataSourceException) {
                Log.d("PlayerError",cause.toString())
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}