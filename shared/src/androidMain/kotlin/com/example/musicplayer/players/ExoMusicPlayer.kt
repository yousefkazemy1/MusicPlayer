package com.example.musicplayer.players

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.model.MusicDuration
import com.example.musicplayer.model.AudioInfo
import com.example.musicplayer.utils.extractAudioFileInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExoMusicPlayer(
    private val context: Context,
) : MusicPlayer {
    private var exoPlayer: ExoPlayer? = null
    private var audioInfo: AudioInfo? = null

    private val _playerStatus = MutableSharedFlow<Boolean>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val playerStatus = _playerStatus.asSharedFlow()

    private val _mediaTransition = MutableSharedFlow<Long>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val mediaTransition = _mediaTransition.asSharedFlow()

    private val _duration = MutableSharedFlow<MusicDuration>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val duration = _duration.asSharedFlow()

    private val job = CoroutineScope(Dispatchers.IO)

    init {
        createPlayer()
        job.launch {
            while (true) {
                exoPlayer?.let { player ->
                    withContext(Dispatchers.Main) {
                        if (player.isPlaying) {
                            _duration.emit(
                                MusicDuration(
                                    duration = player.duration,
                                    progress = player.currentPosition
                                )
                            )
                        }
                    }
                }
                delay(1_000)
            }
        }
    }

    private fun createPlayer() {
        exoPlayer = getExoPlayer(context).also { it ->
            it.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    job.launch {
                        _playerStatus.emit(isPlaying)
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    extractAudio()
                    mediaItem?.let { media ->
                        job.launch {
                            _mediaTransition.emit(media.mediaId.toLong())
                        }
                    }
                }
            })
        }
    }

    override fun loadMusics(musics: List<Music>) {
        exoPlayer?.setMediaItems(createMediaList(musics))
    }

    override fun getCurrentMusic(): AudioInfo? = audioInfo

    override fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }

    override fun startMusic(index: Int) {
        exoPlayer?.prepare()
        exoPlayer?.seekTo(index, C.TIME_UNSET)
        resumeMusic()
        extractAudio()
    }

    override fun resumeMusic() {
        exoPlayer?.play()
    }

    override fun pauseMusic() {
        exoPlayer?.pause()
    }

    override fun goNextMusic() {
        exoPlayer?.seekToNext()
        extractAudio()
    }

    override fun goPreviousMusic() {
        exoPlayer?.seekToPrevious()
        extractAudio()
    }

    override fun releasePlayer() {
        exoPlayer?.release()
        job.cancel()
    }

    override fun seekTo(progress: Long) {
        exoPlayer?.seekTo(progress)
    }

    private fun getExoPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    private fun createMediaItem(music: Music): MediaItem {
        return MediaItem.Builder()
            .setUri(music.uri)
            .setMediaId(music.id.toString())
            .build()
    }

    private fun createMediaList(musicPlayList: List<Music>): List<MediaItem> {
        return musicPlayList.map { music ->
            createMediaItem(music)
        }
    }

    private fun extractAudio() {
        audioInfo = context.extractAudioFileInfo(
            exoPlayer?.currentMediaItem?.localConfiguration?.uri
        )
    }
}