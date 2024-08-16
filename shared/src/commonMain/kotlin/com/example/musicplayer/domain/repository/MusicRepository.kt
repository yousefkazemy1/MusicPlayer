package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Music

interface MusicRepository {
    suspend fun getMusicsList(): List<Music>
}