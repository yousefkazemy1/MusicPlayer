package com.example.musicplayer.android.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.musicplayer.android.ui.tracks.model.MusicDataState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MusicTracksListView(
    musicTracks: ImmutableList<MusicDataState>,
    onClick: (MusicTrackClickEvents) -> Unit,
) {
    LazyColumn(content = {
        items(musicTracks.size, key = { index -> musicTracks[index].id }) { index ->
            val musicTrackItem = musicTracks[index]
            MusicTrackView(musicTrack = musicTrackItem, index = index, onClick = onClick)
        }
    })
}