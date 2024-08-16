package com.example.musicplayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.musicplayer.utils.registerBroadcastReceiver

class MusicPlayerCallback(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val callback: (MusicPlayerState) -> Unit
): DefaultLifecycleObserver {
    companion object {
        const val ACTION_MEDIA_PREVIOUS = "music.player.MEDIA_PREVIOUS"
        const val ACTION_MEDIA_NEXT = "music.player.MEDIA_NEXT"
        const val ACTION_MEDIA_SEEK = "music.player.MEDIA_SEEK"
        const val ACTION_MEDIA_PLAY = "music.player.MEDIA_PLAY"
        const val ACTION_MEDIA_PAUSE = "music.player.MEDIA_PAUSE"
        const val ACTION_MEDIA_STOP = "music.player.MEDIA_STOP"
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        register()
    }

    private val callbackReceiver = CallbackReceiver()

    private fun register() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_MEDIA_PLAY)
            addAction(ACTION_MEDIA_PAUSE)
            addAction(ACTION_MEDIA_NEXT)
            addAction(ACTION_MEDIA_PREVIOUS)
            addAction(ACTION_MEDIA_STOP)
        }
        context.registerBroadcastReceiver(receiver = callbackReceiver, intentFilter = intentFilter)
    }

    private fun unregister() {
        context.unregisterReceiver(callbackReceiver)
    }

    private inner class CallbackReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                intent?.action?.let {
                    callback(parseMusicPlayerReceiverAction(it))
                }
            } catch (ignore: Exception) {
            }
        }

        private fun parseMusicPlayerReceiverAction(action: String): MusicPlayerState {
            return when (action) {
                ACTION_MEDIA_PREVIOUS -> {
                    MusicPlayerState.Previous
                }

                ACTION_MEDIA_NEXT -> {
                    MusicPlayerState.Next
                }

                ACTION_MEDIA_PAUSE -> {
                    MusicPlayerState.Pause
                }

                ACTION_MEDIA_PLAY -> {
                    MusicPlayerState.Play
                }

                ACTION_MEDIA_SEEK -> {
                    MusicPlayerState.Seek(progress = 0)
                }

                else -> {
                    MusicPlayerState.Stop
                }
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        unregister()
        lifecycle.removeObserver(this)
    }
}