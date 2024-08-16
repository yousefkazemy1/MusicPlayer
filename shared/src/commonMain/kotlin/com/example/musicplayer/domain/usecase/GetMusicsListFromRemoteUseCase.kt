package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.repository.MusicRepository

class GetMusicsListFromRemoteUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): List<Music> {
        return musicRepository.getMusicsList()
    }
}