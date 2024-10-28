package com.istudio.media3.demos.video

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.istudio.media3.data.DataSamples
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoDemoVm(
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState

    private var currentPosition: Long = 0L

    fun initializePlayer() {
        val videoUrl = DataSamples.SAMPLE_ONE_VIDEO_MP4
        if (_playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = exoPlayer.also {
                    val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
                    it.setMediaItem(mediaItem)
                    it.prepare()
                    it.playWhenReady = true
                    it.seekTo(currentPosition)
                    it.addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            handleError(error)
                        }
                    })
                }
                _playerState.value = exoPlayer
            }
        }
    }

    fun savePlayerState() {
        // Here it is useful because we can resume from this point when we visit the same composable
        _playerState.value?.let {
            currentPosition = it.currentPosition
        }
    }

    fun releasePlayer() {
        _playerState.value?.release()
        _playerState.value = null
    }

    private fun handleError(error: PlaybackException) {
        when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                // Handle network connection error
                println("Network connection error")
            }

            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                // Handle file not found error
                println("File not found")
            }

            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
                // Handle decoder initialization error
                println("Decoder initialization error")
            }

            else -> {
                // Handle other types of errors
                println("Other error: ${error.message}")
            }
        }
    }

}