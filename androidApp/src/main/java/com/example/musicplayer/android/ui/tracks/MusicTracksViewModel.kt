package com.example.musicplayer.android.ui.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.android.ui.tracks.model.MusicDataState
import com.example.musicplayer.android.ui.tracks.model.mapToUIState
import com.example.musicplayer.android.ui.tracks.model.mapToDataState
import com.example.musicplayer.domain.usecase.GetMusicTracksUseCase
import com.example.musicplayer.players.MusicPlayer
import com.example.musicplayer.services.MusicPlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicTracksViewModel(
    private val tracksUseCase: GetMusicTracksUseCase,
    private val musicPlayerService: MusicPlayerService,
    private val musicPlayer: MusicPlayer,
) : ViewModel() {
    private val _musicTracks = MutableStateFlow<List<MusicDataState>>(emptyList())
    val musicTracks = _musicTracks.asStateFlow()

    private val _currentTrack = MutableStateFlow<MusicDataState?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    val playerStatus = musicPlayer.playerStatus
    val duration = musicPlayer.duration.map {
        it.mapToDataState()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val musics = tracksUseCase()
            _musicTracks.value = musics.mapToUIState()
            if (musics.isNotEmpty()) {
                _currentTrack.value = _musicTracks.value[0]
            }
            withContext(Dispatchers.Main) {
                musicPlayer.loadMusics(musics)
            }
        }

        viewModelScope.launch {
            musicPlayer.mediaTransition.collect { mediaId ->
                musicPlayerService.changeMusicNotification()
                val mediaItem = _musicTracks.value.find { it.id == mediaId }
                _currentTrack.value = mediaItem
            }
        }
    }

    fun startMusic(index: Int) {
        musicPlayerService.startMusic(index)
    }

    fun startPauseMusic() {
        if (musicPlayer.getCurrentMusic() == null) {
            musicPlayerService.startMusic(0)
        } else if (musicPlayer.isPlaying()) {
            musicPlayerService.pauseMusic()
        } else {
            musicPlayerService.startMusic()
        }
    }

    fun goNextMusic() {
        musicPlayerService.goNextMusic()
    }

    fun goPreviousMusic() {
        musicPlayerService.goPreviousMusic()
    }

    fun seekMusic(progress: Int) {
        musicPlayerService.seekMusic(progress * 1000L)
    }
}