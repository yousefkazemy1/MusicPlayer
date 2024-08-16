package com.example.musicplayer.services

import com.example.musicplayer.domain.model.Music

actual class MusicPlayerService {
    actual fun startService(musics: List<Music>) {
    }

    actual fun stopService() {
    }

    actual fun isServiceRunning(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun changePlayLists(musics: List<Music>) {
    }

    actual fun playMusic(music: Music): Boolean {
        TODO("Not yet implemented")
    }

    actual fun pauseMusic(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun pauseService() {
    }
}