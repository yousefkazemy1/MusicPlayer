package com.example.musicplayer.services

interface MusicPlayerService {
    fun startMusic(index: Int ?= null)
    fun pauseMusic()
    fun goNextMusic()
    fun goPreviousMusic()
    fun stopService()
    fun seekMusic(progress: Long)
    fun changeMusicNotification()
}