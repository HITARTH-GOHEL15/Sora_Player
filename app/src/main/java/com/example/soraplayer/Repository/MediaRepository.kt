package com.example.soraplayer.Repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.example.soraplayer.Data.MediaFile
import com.example.soraplayer.Data.MediaType
import javax.inject.Inject

class MediaRepository @Inject constructor (private val context: Context) {

    fun getMediaFiles(): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()
        val contentResolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Files.FileColumns.DURATION
        )

        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString()
        )

        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            val idColumns = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumns = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val uriColumns = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val sizeColumns = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dateModifiedColumns =
                it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val durationColumns = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)
            val folderIdColumns = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT)

            while (it.moveToNext()) {
                val id = it.getLong(idColumns)
                val name = it.getString(nameColumns)
                val uri = it.getString(uriColumns)
                val size = it.getLong(sizeColumns)
                val dateModified = it.getLong(dateModifiedColumns)
                val duration = it.getLong(durationColumns)
                val folderId = it.getLong(folderIdColumns)
                val type = when (uri) {
                    "video/mp4", "video/x-matroska" -> MediaType.VIDEO
                    "audio/mpeg", "audio/x-wav" -> MediaType.MUSIC
                    else -> MediaType.OTHER
                }

                val mediaFile = MediaFile(
                    id,
                    name,
                    uri,
                    type,
                    size,
                    dateModified,
                    folderId,
                    duration.takeIf { it != 0L }
                )
                mediaFiles.add(mediaFile)
            }
        }
        return mediaFiles
    }
}