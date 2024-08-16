package com.example.musicplayer.data.remote.response

import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.model.MusicArtist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicResponse(
    @SerialName("page")
    val id: Long,
//    @SerialName("name")
//    val name: String,
//    @SerialName("uri")
//    val uri: String
)

fun List<MusicResponse>.mapToDomain(): List<Music> {
    val musics = mutableListOf<Music>()
    for (musicResponse in this) {
        val music = Music(
            id = musicResponse.id,
            name = "musicResponse.name",
            artist = MusicArtist(name = ""),
            uri = "musicResponse.uri",
            imageUri = ""
        )

        musics.add(music)
    }
    return musics
}