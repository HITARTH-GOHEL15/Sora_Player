package com.example.soraplayer.Data

import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.example.soraplayer.Model.MusicItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import java.io.File

class LocalMusicProvider(
    private val applicationContext: Application
) {
    fun getMediaMusicFlow(
        selection: String? = "${MediaStore.Audio.Media.MIME_TYPE}=?",
        selectionArgs: Array<String>? = arrayOf("audio/mpeg"),
        sortOrder: String? = "${MediaStore.Audio.Media.TITLE} ASC"
    ): Flow<List<MusicItem>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(getMediaMusic(selection, selectionArgs, sortOrder))
            }
        }
        applicationContext.contentResolver.registerContentObserver(MUSIC_COLLECTION_URI, true, observer)
        // initial value
        trySend(getMediaMusic(selection, selectionArgs, sortOrder))
        // close
        awaitClose { applicationContext.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getMediaMusic(
        selection: String? = "${MediaStore.Audio.Media.MIME_TYPE}=?",
        selectionArgs: Array<String>? = arrayOf("audio/mpeg"),
        sortOrder: String? = "${MediaStore.Audio.Media.TITLE} ASC"
    ): List<MusicItem> {
        val musicItems = mutableListOf<MusicItem>()
        applicationContext.contentResolver.query(
            MUSIC_COLLECTION_URI,
            MUSIC_PROJECTION,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val absolutePath = cursor.getString(dataColumn)
                val name = cursor.getStringOrNull(titleColumn) ?: "Unknown Title"
                val artist = cursor.getStringOrNull(artistColumn) ?: "Unknown Artist"
                val album = cursor.getStringOrNull(albumColumn) ?: "Unknown Album"
                val duration = cursor.getLong(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                musicItems.add(
                    MusicItem(
                        id = id,
                        name = name,
                        artist = artist,
                        album = album,
                        duration = duration,
                        uri = ContentUris.withAppendedId(MUSIC_COLLECTION_URI, id),
                        absolutePath = absolutePath,
                        albumArtUri = albumArtUri
                    )
                )
            }
        }
        return musicItems.filter { File(it.absolutePath).exists() }
    }

    fun getMusicItemFromUri(uri: Uri): MusicItem? {
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
        return getMediaMusic(selection, selectionArgs).firstOrNull()
    }

    companion object {

        const val TAG = "LocalMusicProvider"

        val MUSIC_COLLECTION_URI: Uri
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val MUSIC_PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_MODIFIED
        )
    }
}