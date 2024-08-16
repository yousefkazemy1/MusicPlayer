package com.example.musicplayer.services

sealed class MusicPlayerState {
    data object Play : MusicPlayerState()
    data object Pause : MusicPlayerState()
    data object Previous : MusicPlayerState()
    data object Next : MusicPlayerState()
    data class Seek(val progress: Long): MusicPlayerState()
    data object Stop: MusicPlayerState()
}