package com.example.musicplayer.services

import android.content.Context
import android.content.Intent
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.CHANGE_MUSIC_NOTIFICATION
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.MUSIC_KEY
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.MUSIC_NEXT
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.MUSIC_PAUSE
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.MUSIC_PREVIOUS
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.MUSIC_SEEK
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.MUSIC_SEEK_KEY
import com.example.musicplayer.services.AndroidMusicPlayerService.Companion.MUSIC_START

class MusicPlayerServiceImpl(
    private val context: Context,
) : MusicPlayerService {
    private fun getService() = Intent(context, AndroidMusicPlayerService::class.java)

    override fun startMusic(index: Int?) {
        val serviceIntent = getService().apply {
            action = MUSIC_START
            putExtra(MUSIC_KEY, index)
        }
        context.startService(serviceIntent)
    }

    override fun pauseMusic() {
        val serviceIntent = getService().apply {
            action = MUSIC_PAUSE
        }
        context.startService(serviceIntent)
    }

    override fun goNextMusic() {
        val serviceIntent = getService().apply {
            action = MUSIC_NEXT
        }
        context.startService(serviceIntent)
    }

    override fun goPreviousMusic() {
        val serviceIntent = getService().apply {
            action = MUSIC_PREVIOUS
        }
        context.startService(serviceIntent)
    }

    override fun stopService() {
        context.stopService(getService())
    }

    override fun seekMusic(progress: Long) {
        val serviceIntent = getService().apply {
            action = MUSIC_SEEK
            putExtra(MUSIC_SEEK_KEY, progress)
        }
        context.startService(serviceIntent)
    }

    override fun changeMusicNotification() {
        val serviceIntent = getService().apply {
            action = CHANGE_MUSIC_NOTIFICATION
        }
        context.startService(serviceIntent)
    }
}