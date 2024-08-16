package com.example.musicplayer.android.di

import com.example.musicplayer.data.repository.MusicTrackRepositoryImpl
import com.example.musicplayer.domain.repository.MusicTrackRepository
import com.example.musicplayer.domain.usecase.GetMusicTracksUseCase
import com.example.musicplayer.services.MusicPlayerService
import com.example.musicplayer.services.MusicPlayerServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<MusicTrackRepository> {
        MusicTrackRepositoryImpl(androidContext())
    }

    single {
        GetMusicTracksUseCase(get())
    }

    single<MusicPlayerService> {
        MusicPlayerServiceImpl(androidContext())
    }
}
