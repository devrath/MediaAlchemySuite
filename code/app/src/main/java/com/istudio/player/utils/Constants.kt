package com.istudio.player.utils

import com.istudio.player.R

object Constants {
    const val ART_WORK_URL = "https://picsum.photos/512/512"
    const val PLAYBACK_CHANNEL_ID = "media_playback_channel"
    const val NOTIFICATION_ID = 101
    val CHANNEL_NAME = R.string.media_playback_channel_name

    const val LIVE_SOURCE = "https://video-dev.economist.com/media/all/public/vod-live/master.m3u8?url=https%3A%2F%2Fdemo.unified-streaming.com%2Fk8s%2Ffeatures%2Fstable%2Fvideo%2Ftears-of-steel%2Ftears-of-steel.ism%2F.m3u8"
    //const val LIVE_SOURCE = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    //const val LIVE_SOURCE = "https://nasa-i.akamaihd.net/hls/live/253566/NASA-NTV2-Media/master.m3u8" // -> Another error
    //const val LIVE_SOURCE = "https://www.nasa.gov/multimedia/nasatv/NTV-Public-IPS.m3u8" // --> Returns 404

    // ──────────────────────────────
    // HLS (.m3u8) Sample Streams
    // ──────────────────────────────

    /** Apple sample with alternate renditions, metadata, and captions — basic HLS test stream */
    const val APPLE_BIPBOP = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"

    /** Big Buck Bunny in adaptive HLS format — good for testing VOD and bitrate switching */
    const val BIG_BUCK_BUNNY_HLS = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

    /** Sintel movie in HLS format — widely used for adaptive streaming demo and tests */
    const val SINTEL_HLS = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"

    /** Dolby audio stream — tests advanced audio capabilities (5.1, surround) */
    const val DOLBY_HLS = "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8"

    /** A short VOD HLS test — ideal for UI/UX buffering states and fast load times */
    const val VOD_SAMPLE_HLS = "https://mojen.seznam.cz/static/hls/playlist.m3u8"

    /** CBC News (Live) — geo-blocked in some regions — test live HLS stream behavior */
    const val CBC_LIVE = "https://cbcnewshls-lh.akamaihd.net/i/01/streaming/cbcnews_live@107064/master.m3u8"

    /** NASA TV (Live) — reliable public live stream for continuous playback testing */
    const val NASA_LIVE = "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8"

    /** NBC News Live — useful for simulating real-world live TV playback (geo restrictions may apply) */
    const val NBC_LIVE = "https://nbcnews-lh.akamaihd.net/i/nbcnews_1@136203/master.m3u8"

    /** Tears of Steel — open movie in adaptive HLS format — ideal for long-form playback testing */
    const val TEARS_OF_STEEL_HLS = "https://bitdash-a.akamaihd.net/content/tears_of_steel/playlist.m3u8"

    /** Multi-audio (English, Spanish) & Multi-subtitles (EN, ES, FR) stream from Bitmovin */
    const val MULTI_LANG_AUDIO_SUBS = "https://storage.googleapis.com/shaka-demo-assets/angel-one-hls/hls.m3u8"

    /** DASH + HLS with multilingual subtitles (useful if DASH extension added later) */
    const val MULTI_SUBTITLES_TEST = "https://storage.googleapis.com/shaka-demo-assets/angel-one-hls/hls.m3u8"

    /** Alternative: Stream with multiple caption formats (CEA-608, WebVTT) */
    const val CEA608_WEBVTT_STREAM = "https://test-streams.mux.dev/playlist.m3u8"

    // ──────────────────────────────
    // MP4 Sample Streams
    // ──────────────────────────────

    /** Big Buck Bunny (720p) — short clip, useful for testing progressive streaming and buffering */
    const val BIG_BUCK_BUNNY_720P = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4"

    /** Big Buck Bunny (1080p) — larger size, tests smoothness and performance for high-res video */
    const val BIG_BUCK_BUNNY_1080P = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_1MB.mp4"

    /** Sintel movie — 480p MP4 — suitable for general playback and seeking behavior tests */
    const val SINTEL_MP4 = "https://download.blender.org/durian/trailer/sintel_trailer-480p.mp4"

    /** Tears of Steel — full movie in MP4 format — test for buffering, seeking, and performance */
    const val TEARS_OF_STEEL_MP4 = "https://download.blender.org/demo/movies/ToS/tears_of_steel_720p.mp4"

    /** Elephants Dream — 1024px wide MP4 — useful for testing legacy devices or custom aspect ratios */
    const val ELEPHANTS_DREAM = "https://download.blender.org/durian/trailer/ED_1024.mp4"

    /** Very short 5-second clip — great for startup/load time tests and edge case validation */
    const val SHORT_CLIP_MP4 = "https://samplelib.com/lib/preview/mp4/sample-5s.mp4"

    /** Big Buck Bunny in 4K MP4 — excellent for performance, rendering, and display resolution testing */
    const val BIG_BUCK_BUNNY_4K = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/4k/Big_Buck_Bunny_4K_10s_1MB.mp4"

    // ──────────────────────────────
    // Utility Functions
    // ──────────────────────────────

    /**
     * Returns a list of all available HLS (.m3u8) test stream URLs.
     * @return List of HLS stream URLs
     */
    fun hlsSamples(): List<String> = listOf(
        APPLE_BIPBOP,
        BIG_BUCK_BUNNY_HLS,
        SINTEL_HLS,
        DOLBY_HLS,
        VOD_SAMPLE_HLS,
        CBC_LIVE,
        NASA_LIVE,
        NBC_LIVE,
        TEARS_OF_STEEL_HLS,
        MULTI_LANG_AUDIO_SUBS,
        MULTI_SUBTITLES_TEST
    )

    /**
     * Returns a list of all available MP4 test stream URLs.
     * @return List of MP4 stream URLs
     */
    fun mp4Samples(): List<String> = listOf(
        BIG_BUCK_BUNNY_720P,
        BIG_BUCK_BUNNY_1080P,
        SINTEL_MP4,
        TEARS_OF_STEEL_MP4,
        ELEPHANTS_DREAM,
        SHORT_CLIP_MP4,
        BIG_BUCK_BUNNY_4K
    )

    /**
     * Returns all available test stream URLs (both HLS and MP4).
     * @return Combined list of all test streams
     */
    fun all(): List<String> = hlsSamples() + mp4Samples()
}