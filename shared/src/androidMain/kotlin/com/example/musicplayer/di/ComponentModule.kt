package com.example.musicplayer.di

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.musicplayer.data.repository.MusicTrackRepositoryImpl
import com.example.musicplayer.domain.repository.MusicTrackRepository
import com.example.musicplayer.notification.MusicNotificationManager
import com.example.musicplayer.players.ExoMusicPlayer
import com.example.musicplayer.players.MusicPlayer
import com.example.musicplayer.services.MusicPlayerService
import com.example.musicplayer.services.MusicPlayerServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val componentModule = module {
    single {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            androidContext().getSystemService(NotificationManager::class.java)
        } else {
            androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    single {
        MusicNotificationManager(get())
    }

    single<MusicPlayer> {
        ExoMusicPlayer(androidContext())
    }

    single<MusicPlayerService> {
        MusicPlayerServiceImpl(androidContext())
    }

    single<MusicTrackRepository> {
        MusicTrackRepositoryImpl(androidContext())
    }
}