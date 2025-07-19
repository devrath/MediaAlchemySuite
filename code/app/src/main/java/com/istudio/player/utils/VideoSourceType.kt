package com.istudio.player.utils

import com.istudio.player.utils.Constants.BIG_BUCK_BUNNY_720P
import com.istudio.player.utils.Constants.MULTI_LANG_AUDIO_SUBS

sealed class VideoSourceType(val label: String, val url: String) {
    data object MP4 : VideoSourceType("MP4", BIG_BUCK_BUNNY_720P)
    data object HLS : VideoSourceType("HLS", MULTI_LANG_AUDIO_SUBS)
    data object LIVE : VideoSourceType("Live", "")
}
