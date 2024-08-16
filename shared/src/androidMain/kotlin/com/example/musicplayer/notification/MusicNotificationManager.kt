package com.example.musicplayer.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.musicplayer.R
import com.example.musicplayer.model.AudioInfo
import com.example.musicplayer.services.AndroidMusicPlayerService
import com.example.musicplayer.services.MusicPlayerCallback.Companion.ACTION_MEDIA_NEXT
import com.example.musicplayer.services.MusicPlayerCallback.Companion.ACTION_MEDIA_PAUSE
import com.example.musicplayer.services.MusicPlayerCallback.Companion.ACTION_MEDIA_PLAY
import com.example.musicplayer.services.MusicPlayerCallback.Companion.ACTION_MEDIA_PREVIOUS
import com.example.musicplayer.services.MusicPlayerCallback.Companion.ACTION_MEDIA_STOP
import com.example.musicplayer.ui.CoreActivity
import com.example.musicplayer.utils.isSdkEqualOrGreaterThanO

class MusicNotificationManager(
    private val notificationManager: NotificationManager,
) {
    companion object {
        private const val MUSIC_PLAYER_SERVICE_CHANNEL_ID = "MusicPlayerService"
        private const val MUSIC_PLAYER_SERVICE_CHANNEL_NAME = "Music Player"
        private const val TAG = "MediaPlaybackReceiver"
    }

    init {
        createMusicNotificationChannel()
    }

    fun createMusicNotification(
        context: Context,
        audioInfo: AudioInfo?,
        mediaSession: MediaSessionCompat,
        isPlaying: Boolean = false,
    ): Notification {
        val notificationBuilder = createMusicBaseNotificationBuilder(
            context = context,
            audioInfo = audioInfo,
            mediaSession = mediaSession,
            isPlaying = isPlaying,
        )

        addLargeIcon(
            context = context,
            notificationBuilder = notificationBuilder,
            icon = audioInfo?.image
        )

        addPreviousButtonAction(context = context, notificationBuilder = notificationBuilder)
        addPlayAndPauseButtonAction(
            context = context,
            notificationBuilder = notificationBuilder,
            isPlaying = isPlaying,
        )
        addNextButtonAction(context = context, notificationBuilder = notificationBuilder)

        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        return notificationBuilder.build()
    }

    private fun createMusicBaseNotificationBuilder(
        context: Context,
        audioInfo: AudioInfo?,
        mediaSession: MediaSessionCompat,
        isPlaying: Boolean = false,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, MUSIC_PLAYER_SERVICE_CHANNEL_ID)
            .setOngoing(isPlaying)
            .setAutoCancel(isPlaying)
            .setContentTitle(audioInfo?.title)
            .setContentText(audioInfo?.artist)
            .setSubText(audioInfo?.album)
            .setSmallIcon(R.drawable.player)
            .setContentIntent(createContentIntent(context))
            .setStyle(MediaStyle().setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0))
            .setDeleteIntent(createDeleteIntent(context))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null)
            .setSilent(true)
    }

    private fun addLargeIcon(
        context: Context,
        notificationBuilder: NotificationCompat.Builder,
        icon: Bitmap?,
    ) {
        icon?.let {
            notificationBuilder.setLargeIcon(it)
        } ?: run {
            notificationBuilder.setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.nocover_small
                )
            )
        }
    }

    private fun addPreviousButtonAction(
        context: Context,
        notificationBuilder: NotificationCompat.Builder,
    ) {
        notificationBuilder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_previous,
                context.getString(R.string.previous),
                createButtonIntent(context, ACTION_MEDIA_PREVIOUS)
            )
        )
    }

    private fun addPlayAndPauseButtonAction(
        context: Context,
        notificationBuilder: NotificationCompat.Builder,
        isPlaying: Boolean,
    ) {
        if (isPlaying) {
            notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    context.getString(R.string.pause),
                    createButtonIntent(context, ACTION_MEDIA_PAUSE)
                )
            )
        } else {
            notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    context.getString(R.string.play),
                    createButtonIntent(context, ACTION_MEDIA_PLAY)
                )
            )
        }
    }

    private fun addNextButtonAction(
        context: Context,
        notificationBuilder: NotificationCompat.Builder,
    ) {
        notificationBuilder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_next,
                context.getString(R.string.next),
                createButtonIntent(context, ACTION_MEDIA_NEXT)
            )
        )
    }

    private fun createContentIntent(context: Context): PendingIntent {
        val intent = Intent(context, CoreActivity::class.java).apply {
            putExtra("name: ", "name")
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createDeleteIntent(context: Context): PendingIntent {
        val deleteIntent = Intent(
            context,
            AndroidMusicPlayerService::class.java
        )
        deleteIntent.action = ACTION_MEDIA_STOP
        return PendingIntent.getService(
            context,
            0,
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createButtonIntent(context: Context, buttonAction: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            0,
            Intent(buttonAction),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createMusicNotificationChannel() {
        if (!isCreatingChannelsNeeded()) return

        if (isMusicChannelCreated()) return

        val musicServiceNotificationChannel = NotificationChannel(
            MUSIC_PLAYER_SERVICE_CHANNEL_ID,
            MUSIC_PLAYER_SERVICE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(musicServiceNotificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isMusicChannelCreated(): Boolean {
        val existingNotificationChannel = notificationManager.getNotificationChannel(
            MUSIC_PLAYER_SERVICE_CHANNEL_ID
        )
        return existingNotificationChannel != null
    }

    private fun isCreatingChannelsNeeded() = isSdkEqualOrGreaterThanO()
}