package com.example.musicplayer.android.ui.tracks.model

import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.model.MusicArtist

data class MusicDataState(
    val id: Long,
    val title: String,
    val artistName: String,
    val musicUri: String,
    val imageUri: String,
)

fun List<Music>.mapToUIState(): List<MusicDataState> {
    val musicTracksUIStateList = ArrayList<MusicDataState>()
    forEach { music ->
        val musicTrackUIState = MusicDataState(
            id = music.id,
            title = music.name,
            artistName = music.artist.name,
            musicUri = music.uri,
            imageUri = music.imageUri
        )
        musicTracksUIStateList.add(musicTrackUIState)
    }

    return musicTracksUIStateList.toList()
}

fun MusicDataState.mapToDomain(): Music {
    return Music(
        id = id,
        name = title,
        artist = MusicArtist(name = artistName),
        uri = musicUri,
        imageUri = imageUri
    )
}