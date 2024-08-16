package com.example.musicplayer.android.ui

import android.app.Application
import com.example.musicplayer.android.di.appModule
import com.example.musicplayer.di.componentModule
import com.example.musicplayer.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import viewModelModule

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApp)
            modules(appModule, componentModule, networkModule, viewModelModule)
        }
    }
}