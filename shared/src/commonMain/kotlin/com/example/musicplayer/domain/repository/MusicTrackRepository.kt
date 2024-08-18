package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.model.MusicOrder
import com.example.musicplayer.domain.model.MusicOrderType

interface MusicTrackRepository {
    suspend fun getMusicTracks(
        sortOrder: MusicOrder = MusicOrder.DATE,
        sortType: MusicOrderType = MusicOrderType.DESC
    ): List<Music>
}