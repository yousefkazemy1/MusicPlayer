package com.example.musicplayer.di

import com.example.musicplayer.data.remote.ktorInstance
import com.example.musicplayer.data.repository.MusicRepositoryImpl
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.domain.usecase.GetMusicsListFromRemoteUseCase
import org.koin.dsl.module

val networkModule = module {
    single { ktorInstance }

    single<MusicRepository> { MusicRepositoryImpl(get()) }

    single {
        GetMusicsListFromRemoteUseCase(get())
    }
}