package com.example.musicplayer.domain.model

data class Music(
    val id: Long,
    val name: String,
    val artist: MusicArtist,
    val uri: String,
    val imageUri: String
)