package com.example.musicplayer.players

import androidx.collection.ObjectList
import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.model.MusicDuration
import com.example.musicplayer.model.AudioInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MusicPlayer {
    val playerStatus: SharedFlow<Boolean>
    val mediaTransition: SharedFlow<Long>
    val duration: SharedFlow<MusicDuration>
    fun loadMusics(musics: List<Music>)
    fun getCurrentMusic(): AudioInfo?
    fun isPlaying(): Boolean
    fun startMusic(index: Int)
    fun resumeMusic()
    fun pauseMusic()
    fun goNextMusic()
    fun goPreviousMusic()
    fun releasePlayer()
    fun seekTo(progress: Long)
}