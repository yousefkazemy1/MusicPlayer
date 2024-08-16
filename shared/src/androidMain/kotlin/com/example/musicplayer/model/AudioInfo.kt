package com.example.musicplayer.model

import android.graphics.Bitmap

data class AudioInfo(
    val uri: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val duration: Long = 0,
    val image: Bitmap? = null
)