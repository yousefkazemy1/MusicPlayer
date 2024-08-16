package com.example.musicplayer.android.ui.tracks.model

import com.example.musicplayer.domain.model.MusicDuration

data class MusicDurationDataState(
    val duration: Int = 0,
    val progress: Int = 0
)

fun MusicDuration.mapToDataState() = MusicDurationDataState(
    duration = (duration / 1000).toInt(),
    progress = (progress / 1000).toInt()
)