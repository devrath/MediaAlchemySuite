package com.istudio.media3.data

import com.istudio.media3.demos.audio.model.TrackItem

interface Tracks {
    fun trackList() : List<TrackItem>
}