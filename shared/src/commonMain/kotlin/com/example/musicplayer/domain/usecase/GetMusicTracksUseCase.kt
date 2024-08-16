package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.repository.MusicTrackRepository
import com.example.musicplayer.provider.MusicOrder
import com.example.musicplayer.provider.MusicOrderType

class GetMusicTracksUseCase(
    private val musicTrackRepository: MusicTrackRepository
) {
    suspend operator fun invoke(
        sortOrder: MusicOrder = MusicOrder.DATE,
        sortType: MusicOrderType = MusicOrderType.DESC,
    ): List<Music> {
        return musicTrackRepository.getMusicTracks(
            sortOrder = sortOrder,
            sortType = sortType
        )
    }
}