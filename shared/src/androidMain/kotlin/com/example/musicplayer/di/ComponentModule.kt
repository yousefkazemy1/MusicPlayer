package com.example.musicplayer.di

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.musicplayer.notification.MusicNotificationManager
import com.example.musicplayer.players.ExoMusicPlayer
import com.example.musicplayer.players.MusicPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val componentModule = module {
    single<MusicPlayer> {
        ExoMusicPlayer(androidContext())
    }

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
}