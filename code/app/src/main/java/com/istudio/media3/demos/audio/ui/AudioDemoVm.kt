package com.istudio.media3.demos.audio.ui

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.istudio.media3.data.Tracks
import com.istudio.media3.demos.audio.enum.ControlButtons
import com.istudio.media3.demos.audio.model.TrackItem
import com.istudio.media3.demos.audio.notificaton.MediaNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn



@OptIn(androidx.media3.common.util.UnstableApi::class)
class AudioDemoVm(
    val player: ExoPlayer,
    tracks: Tracks
) : ViewModel() {

    companion object {
        private const val TAG = "Media3AppTag"
        const val SESSION_INTENT_REQUEST_CODE = 0
    }

    // Data from the data-source
    private val playlist = tracks.trackList()

    private val _currentPlayingIndex = MutableStateFlow(0)
    val currentPlayingIndex = _currentPlayingIndex.asStateFlow()

    private val _totalDurationInMS = MutableStateFlow(0L)
    val totalDurationInMS = _totalDurationInMS.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    val uiState: StateFlow<PlayerUIState> =
        MutableStateFlow(PlayerUIState.Tracks(playlist)).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUIState.Loading
        )

    // Control the notification displayed
    private lateinit var notificationManager: MediaNotificationManager
    // Media session notifies the underlying player
    private lateinit var mediaSession: MediaSession

    private var isStarted = false


    /**
     * Prepare the player state
     */
    fun preparePlayer(context:Context) {
        // Prepare the attributes
        val audioAttributes = AudioAttributes.Builder().apply {
            setUsage(C.USAGE_MEDIA) // Usage is for media
            setContentType(C.AUDIO_CONTENT_TYPE_MUSIC) // Usage is for media
        }.build()

        player.apply {
            setAudioAttributes(audioAttributes, true) // Set attributes
            repeatMode = Player.REPEAT_MODE_ALL // Set the repeat mode
            addListener(playerListener)
        }

        setupPlaylist(context)
    }

    @OptIn(UnstableApi::class)
    private fun setupPlaylist(context: Context) {

        val videoItems: ArrayList<MediaSource> = arrayListOf()
        val dataSource : List<TrackItem> = playlist


        dataSource.forEach {

            // ***************** Prepare the items to be added *****************
            val dataSourceFactory = DefaultDataSource.Factory(context)

            val mediaId = it.id
            val displayImage = Uri.parse(it.teaserUrl)
            val title = it.title
            val artistName = it.artistName
            val trackUri = Uri.parse(it.audioUrl)
            // ***************** Prepare the items to be added *****************

            // META-DATA
            val mediaMetaData = MediaMetadata.Builder().apply {
                setArtworkUri(displayImage)
                setTitle(title)
                setAlbumArtist(artistName)
            }.build()

            // MEDIA-ITEM
            val mediaItem = MediaItem.Builder().apply {
                setUri(trackUri)
                setMediaId(mediaId)
                setMediaMetadata(mediaMetaData)
            }.build()

            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

            videoItems.add(mediaSource)
        }

        onStart(context)

        player.apply {
            playWhenReady = false
            setMediaSources(videoItems)
            prepare()
        }
    }

    fun updatePlaylist(action: ControlButtons) {
        when (action) {
            ControlButtons.Play -> if (player.isPlaying) player.pause() else player.play()
            ControlButtons.Next -> player.seekToNextMediaItem()
            ControlButtons.Rewind -> player.seekToPreviousMediaItem()
        }
    }

    fun updatePlayerPosition(position: Long) {
        player.seekTo(position)
    }

    private fun onStart(context: Context) {
        if (isStarted) return

        isStarted = true

        // Build a PendingIntent that can be used to launch the UI.
        val sessionPendingIntent = preparePendingIntent(context)

        // Create a new MediaSession.
        mediaSession = prepareMediaSession(context, sessionPendingIntent)

        /**
         * The notification manager will use our player and media session to decide when to post
         * notifications. When notifications are posted or removed our listener will be called, this
         * allows us to promote the service to foreground (required so that we're not killed if
         * the main UI is not visible).
         */
        notificationManager =
            MediaNotificationManager(
                context, mediaSession.token, player,
                PlayerNotificationListener()
            )


        notificationManager.showNotificationForPlayer(player)
    }

    /**
     * Destroy audio notification
     */
    fun onDestroy() {
        onClose()
        player.release()
    }

    /**
     * Close audio notification
     */
    private fun onClose() {
        if (!isStarted) return

        isStarted = false
        // Release the media session
        mediaSession.run { release() }
        // Hide notification
        notificationManager.hideNotification()
        // Free ExoPlayer resources.
        player.removeListener(playerListener)
    }

    /**
     * *************************** Listeners ***************************
     */
    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int, notification: Notification, ongoing: Boolean
        ) {
            Log.d(TAG, "notificationId: $notificationId"
                .plus("\n")
                .plus("ongoing: $ongoing"))
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            Log.d(TAG, "notificationId: $notificationId"
                .plus("\n")
                .plus("dismissedByUser: $dismissedByUser"))
        }
    }

    /**
     * Listen to events from ExoPlayer.
     */
    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.d(TAG, "onPlaybackStateChanged: $playbackState")
            super.onPlaybackStateChanged(playbackState)
            syncPlayerFlows()
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> notificationManager.showNotificationForPlayer(player)
                else ->  notificationManager.hideNotification()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Log.d(TAG, "onMediaItemTransition: ${mediaItem?.mediaMetadata?.title}")
            super.onMediaItemTransition(mediaItem, reason)
            syncPlayerFlows()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d(TAG, "onIsPlayingChanged: $isPlaying")
            super.onIsPlayingChanged(isPlaying)
            _isPlaying.value = isPlaying
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "Error: ${error.message}")
        }
    }
    /**
     * *************************** Listeners ***************************
     */

    private fun syncPlayerFlows() {
        _currentPlayingIndex.value = player.currentMediaItemIndex
        _totalDurationInMS.value = player.duration.coerceAtLeast(0L)
    }

    /**
     * Prepare the media session
     */
    private fun prepareMediaSession(
        context: Context,
        sessionPendingIntent: PendingIntent?
    ) = MediaSession.Builder(context, player)
        .setSessionActivity(sessionPendingIntent!!).build()

    /**
     * Prepare the pending intent
     */
    private fun preparePendingIntent(context: Context): PendingIntent? {
        return context.packageManager?.getLaunchIntentForPackage(context.packageName)
            ?.let { sessionIntent ->
                PendingIntent.getActivity(
                    context, SESSION_INTENT_REQUEST_CODE,
                    sessionIntent, PendingIntent.FLAG_IMMUTABLE
                )
            }
    }
}



/**
 *  Player UI states
 */
sealed interface PlayerUIState {
    data class Tracks(val items: List<TrackItem>) : PlayerUIState
    data object Loading : PlayerUIState
}