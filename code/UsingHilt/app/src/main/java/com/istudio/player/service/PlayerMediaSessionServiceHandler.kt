package com.istudio.player.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerMediaSessionServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : Player.Listener {
    private val _audioState: MutableStateFlow<JetAudioState> =
        MutableStateFlow(JetAudioState.Initial)
    val audioState: StateFlow<JetAudioState> = _audioState.asStateFlow()

    private var progressJob: Job? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        exoPlayer.addListener(this)
    }

    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setMediaItemList(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    suspend fun onPlayerEvents(
        playerEvent: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0,
    ) {
        when (playerEvent) {
            is PlayerEvent.Backward -> exoPlayer.seekBack()
            is PlayerEvent.Forward -> exoPlayer.seekForward()
            is PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            is PlayerEvent.PlayPause -> playOrPause()
            is PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            is PlayerEvent.SelectedAudioChange -> {
                if (selectedAudioIndex == exoPlayer.currentMediaItemIndex) {
                    playOrPause()
                } else {
                    exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                    _audioState.value = JetAudioState.Playing(isPlaying = true)
                    exoPlayer.playWhenReady = true
                    startProgressUpdate()
                }
            }
            is PlayerEvent.Stop -> release()
            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo((exoPlayer.duration * playerEvent.newProgress).toLong())
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _audioState.value =
                JetAudioState.Buffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY -> _audioState.value =
                JetAudioState.Ready(exoPlayer.duration)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = JetAudioState.Playing(isPlaying = isPlaying)
        _audioState.value = JetAudioState.CurrentPlaying(exoPlayer.currentMediaItemIndex)

        if (isPlaying) {
            startProgressUpdate()
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _audioState.value = JetAudioState.Playing(isPlaying = true)
            startProgressUpdate()
        }
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (true) {
                delay(500)
                _audioState.value = JetAudioState.Progress(exoPlayer.currentPosition)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        _audioState.value = JetAudioState.Playing(isPlaying = false)
    }

    fun release() {
        coroutineScope.cancel()
        exoPlayer.removeListener(this)
        stopProgressUpdate()
    }
}

sealed class PlayerEvent {
    data object PlayPause : PlayerEvent()
    data object SelectedAudioChange : PlayerEvent()
    data object Backward : PlayerEvent()
    data object SeekToNext : PlayerEvent()
    data object Forward : PlayerEvent()
    data object SeekTo : PlayerEvent()
    data object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class JetAudioState {
    data object Initial : JetAudioState()
    data class Ready(val duration: Long) : JetAudioState()
    data class Progress(val progress: Long) : JetAudioState()
    data class Buffering(val progress: Long) : JetAudioState()
    data class Playing(val isPlaying: Boolean) : JetAudioState()
    data class CurrentPlaying(val mediaItemIndex: Int) : JetAudioState()
}