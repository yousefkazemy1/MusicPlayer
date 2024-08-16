package com.example.musicplayer.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.musicplayer.model.AudioInfo

class MusicMediaSessionController(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val callback: (MusicPlayerState) -> Unit,
) : DefaultLifecycleObserver {
    companion object {
        const val TAG = "MusicPlayer"
    }

    lateinit var mediaSession: MediaSessionCompat
        private set
    private lateinit var playbackState: PlaybackStateCompat.Builder

    init {
        lifecycle.addObserver(this)
        initializeMediaSession()
    }

    private fun initializeMediaSession() {
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE
        )
        mediaSession = MediaSessionCompat(context, TAG, null, pendingIntent)
        createPlaybackState()
        addCallbacks()
        mediaSession.isActive = true
    }

    private fun createPlaybackState() {
        playbackState = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_SEEK_TO or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    }

    private fun addCallbacks() {
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                callback(MusicPlayerState.Play)
            }

            override fun onPause() {
                callback(MusicPlayerState.Pause)
            }

            override fun onSkipToNext() {
                callback(MusicPlayerState.Next)
            }

            override fun onSkipToPrevious() {
                callback(MusicPlayerState.Previous)
            }

            override fun onSeekTo(pos: Long) {
                callback(MusicPlayerState.Seek(progress = pos))
            }
        })
    }

    fun updatePlaybackState(state: Int, audioInfo: AudioInfo?, position: Long = -1) {
        updatePlaybackState(state = state, position = position)
        val meta: MediaMetadataCompat.Builder = MediaMetadataCompat.Builder()
            .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, audioInfo?.image)
            .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, audioInfo?.artist)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, audioInfo?.duration ?: 0)
            .putString(MediaMetadata.METADATA_KEY_TITLE, audioInfo?.title)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, audioInfo?.album)
        mediaSession.setMetadata(meta.build())
    }

    private fun updatePlaybackState(state: Int, position: Long = -1) {
        when {
            position >= 0 -> {
                val lastPlaybackSpeed = mediaSession.controller.playbackState?.playbackSpeed ?: 0f
                val lastPlaybackState = mediaSession.controller.playbackState?.state ?: PlaybackStateCompat.STATE_PLAYING
                playbackState.setState(lastPlaybackState, position, lastPlaybackSpeed)
            }
            state == PlaybackStateCompat.STATE_PLAYING -> {
                val lastPlaybackPosition = mediaSession.controller.playbackState?.position ?: 0L
                playbackState.setState(state, lastPlaybackPosition, 1f)
            }
            state == PlaybackStateCompat.STATE_PAUSED -> {
                val lastPlaybackPosition = mediaSession.controller.playbackState?.position ?: 0L
                playbackState.setState(state, lastPlaybackPosition, 0f)
            }
            else -> {
                val lastPlaybackSpeed = mediaSession.controller.playbackState?.playbackSpeed ?: 0f
                val lastPlaybackState = mediaSession.controller.playbackState?.state ?: PlaybackStateCompat.STATE_PLAYING
                playbackState.setState(lastPlaybackState, 0, lastPlaybackSpeed)
            }
        }
        mediaSession.setPlaybackState(playbackState.build())
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        lifecycle.removeObserver(this)
    }
}