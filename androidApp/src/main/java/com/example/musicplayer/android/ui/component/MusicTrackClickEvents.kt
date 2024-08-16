package com.example.musicplayer.android.ui.component

import com.example.musicplayer.android.ui.tracks.model.MusicDataState

sealed class MusicTrackClickEvents(musicTrackUIState: MusicDataState?) {
    data class OnItemClick(val musicTrackUIState: MusicDataState? = null, val index: Int) :
        MusicTrackClickEvents(musicTrackUIState)

    data class OnItemLongClick(val musicTrackUIState: MusicDataState? = null) :
        MusicTrackClickEvents(musicTrackUIState)

    data class OnItemMoreClick(val musicTrackUIState: MusicDataState? = null) :
        MusicTrackClickEvents(musicTrackUIState)
}