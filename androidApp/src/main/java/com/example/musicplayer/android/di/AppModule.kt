package com.example.musicplayer.android.di

import com.example.musicplayer.domain.usecase.GetMusicTracksUseCase
import org.koin.dsl.module

val appModule = module {
    single {
        GetMusicTracksUseCase(get())
    }
}
