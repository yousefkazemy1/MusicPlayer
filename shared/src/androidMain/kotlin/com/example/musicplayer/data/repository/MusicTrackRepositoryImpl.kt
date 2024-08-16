package com.example.musicplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.musicplayer.domain.model.Music
import com.example.musicplayer.domain.model.MusicArtist
import com.example.musicplayer.domain.repository.MusicTrackRepository
import com.example.musicplayer.provider.MusicOrder
import com.example.musicplayer.provider.MusicOrderType

class MusicTrackRepositoryImpl(
    private val context: Context,
) : MusicTrackRepository {

    companion object {
        private val AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        private val PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST
        )
        private const val SELECTION = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    }

    override suspend fun getMusicTracks(
        sortOrder: MusicOrder,
        sortType: MusicOrderType,
    ): List<Music> {
        val cursor = context.contentResolver.query(
            AUDIO_URI,
            PROJECTION,
            SELECTION,
            null,
            createSortOrder(sortOrder, sortType)
        )
        val musics = parseMusicTracks(cursor)
        cursor?.close()
        return musics
    }

    private fun parseMusicTracks(cursor: Cursor?): List<Music> {
        if (cursor == null) return emptyList()

        val musics = mutableListOf<Music>()
        while (cursor.moveToNext()) {
            val music = parseMusicTrack(cursor)
            musics.add(music)
        }

        return musics
    }

    private fun parseMusicTrack(cursor: Cursor): Music {
        val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
        val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val albumIDColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

        val id = cursor.getLong(idColumn)
        val name = cursor.getString(nameColumn)
        val artist = cursor.getString(artistColumn)
        val albumId = cursor.getLong(albumIDColumn)

        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
        )
        val albumArtUri = Uri.withAppendedPath(
            Uri.parse("content://media/external/audio/albumart"),
            albumId.toString()
        )

        return Music(
            id = id,
            name = name,
            artist = MusicArtist(name = artist),
            uri = uri.toString(),
            imageUri = albumArtUri.toString()
        )
    }

    private fun createSortOrder(order: MusicOrder, type: MusicOrderType) = when (order) {
        MusicOrder.DATE -> {
            "${MediaStore.Audio.Media.DATE_ADDED} ${createSortOrderType(type)}"
        }

        else -> {
            "${MediaStore.Audio.Media.DISPLAY_NAME} ${createSortOrderType(type)}"
        }
    }

    private fun createSortOrderType(type: MusicOrderType) = when (type) {
        MusicOrderType.ASC -> {
            "ASC"
        }

        else -> {
            "DESC"
        }
    }
}