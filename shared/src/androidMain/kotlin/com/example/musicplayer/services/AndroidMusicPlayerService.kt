package com.example.musicplayer.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaMetadata
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.musicplayer.model.AudioInfo
import com.example.musicplayer.notification.MusicNotificationManager
import com.example.musicplayer.players.MusicPlayer
import com.example.musicplayer.services.MusicPlayerCallback.Companion.ACTION_MEDIA_STOP
import org.koin.android.ext.android.inject


class AndroidMusicPlayerService : Service(), LifecycleOwner {

    override val lifecycle = LifecycleRegistry(this)

    companion object {
        const val MUSIC_START = "music_start"
        const val MUSIC_PAUSE = "music_stop"
        const val MUSIC_NEXT = "music_next"
        const val MUSIC_PREVIOUS = "music_previous"
        const val MUSIC_SEEK = "music_seek"
        const val CHANGE_MUSIC_NOTIFICATION = "change_music_notification"
        const val MUSIC_KEY = "music"
        const val MUSIC_SEEK_KEY = "music_seek_key"
        const val NOTIFICATION_ID = 1
    }

    private val musicPlayer: MusicPlayer by inject()
    private val musicNotificationManager: MusicNotificationManager by inject()

    private lateinit var musicPlayerCallback: MusicPlayerCallback
    private lateinit var mediaSessionController: MusicMediaSessionController

    private val playerStateCallback: (MusicPlayerState) -> Unit = { state ->
        when (state) {
            is MusicPlayerState.Play -> startPlay()
            is MusicPlayerState.Pause -> pausePlay()
            is MusicPlayerState.Previous -> goPreviousMusic()
            is MusicPlayerState.Next -> goNextMusic()
            is MusicPlayerState.Seek -> seekMusic(progress = state.progress)
            is MusicPlayerState.Stop -> { }
        }
    }
    override fun onCreate() {
        super.onCreate()
        musicPlayerCallback = MusicPlayerCallback(
            context = this, lifecycle = lifecycle, callback = playerStateCallback
        )
        mediaSessionController = MusicMediaSessionController(
            context = this, lifecycle = lifecycle, callback = playerStateCallback
        )
        lifecycle.currentState = Lifecycle.State.CREATED
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MUSIC_START -> {
                val musicIndex = intent.extras?.getInt(MUSIC_KEY, -1)
                musicIndex?.let {
                    startPlay(musicIndex)
                } ?: startPlay(-1)
            }
            MUSIC_PAUSE -> {
                pausePlay()
            }
            MUSIC_NEXT -> {
                goNextMusic()
            }
            MUSIC_PREVIOUS -> {
                goPreviousMusic()
            }
            MUSIC_SEEK -> {
                intent.extras?.getLong(MUSIC_SEEK_KEY, 0)?.also {
                    seekMusic(it)
                }
            }
            CHANGE_MUSIC_NOTIFICATION -> {
                createNotification(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
            }
            ACTION_MEDIA_STOP -> {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun startPlay(index: Int = -1) {
        if (index != -1) {
            musicPlayer.startMusic(index)
        } else {
            musicPlayer.resumeMusic()
        }
        createNotification(PlaybackStateCompat.STATE_PLAYING)
    }

    private fun pausePlay() {
        musicPlayer.pauseMusic()
        createNotification(PlaybackStateCompat.STATE_PAUSED)
    }

    private fun goNextMusic() {
        musicPlayer.goNextMusic()
    }

    private fun goPreviousMusic() {
        musicPlayer.goPreviousMusic()
    }

    private fun seekMusic(progress: Long) {
        musicPlayer.seekTo(progress)
        createNotification(PlaybackStateCompat.STATE_PLAYING, progress = progress)
    }

    private fun createNotification(state: Int, progress: Long = -1) {
        val audioInfo = musicPlayer.getCurrentMusic()
        mediaSessionController.updatePlaybackState(
            state = state,
            audioInfo = audioInfo,
            position = progress
        )
        val notification = musicNotificationManager.createMusicNotification(
            context = this,
            audioInfo = audioInfo,
            mediaSession = mediaSessionController.mediaSession,
            isPlaying = state == PlaybackStateCompat.STATE_PLAYING
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.currentState = Lifecycle.State.DESTROYED
    }
}

