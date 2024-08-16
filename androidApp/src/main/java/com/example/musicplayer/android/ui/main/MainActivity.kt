package com.example.musicplayer.android.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.musicplayer.android.ui.theme.AppTheme
import com.example.musicplayer.android.ui.tracks.MusicTracksScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MusicTracksScreen()
            }
        }
    }
}