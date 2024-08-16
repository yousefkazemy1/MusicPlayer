package com.example.musicplayer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.musicplayer.model.AudioInfo

fun Context.extractAudioFileInfo(uri: Uri?): AudioInfo? {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(this, uri)

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown"
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown"
        val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown"
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "Unknown"

        return AudioInfo(
            uri = uri.toString(),
            title = title,
            artist = artist,
            album = album,
            duration = duration.toLong(),
            image = extractAudioFileImage(retriever)
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        retriever.release()
    }
}

private fun extractAudioFileImage(retriever: MediaMetadataRetriever): Bitmap? {
    try {
        return retriever.embeddedPicture?.let { pictureBytes ->
            BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.size)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}