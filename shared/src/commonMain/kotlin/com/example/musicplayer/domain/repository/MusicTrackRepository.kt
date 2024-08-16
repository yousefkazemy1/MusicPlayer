package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.provider.MusicOrder
import com.example.musicplayer.provider.MusicOrderType

interface MusicTrackRepository {
    suspend fun getMusicTracks(
        sortOrder: MusicOrder = MusicOrder.DATE,
        sortType: MusicOrderType = MusicOrderType.DESC
    ): List<Music>
}