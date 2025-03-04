package com.example.soraplayer.Data

import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.database.getStringOrNull
import com.example.soraplayer.Model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class LocalMediaProvider(
    private val applicationContext: Application
) {

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    // Implementation for LocalMediaProvider
    fun getVideoItemFromContentUri(uri: Uri): VideoItem?{

        var displayName:String? = null

        if(uri.scheme == "content"){
            Log.d(TAG, "Uri scheme is content")
            applicationContext.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                displayName = cursor.getStringOrNull(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }else{
            Log.d(TAG, "Uri scheme is file")
            displayName = uri.path?.split("/")?.lastOrNull().toString()
        }

        return if(displayName != null){
            Log.d(TAG, "display name is not null")
            getMediaVideo().first{ displayName == it.name }
        }else{
            Log.d(TAG, "display name is null")
            null
        }
    }

    fun getMediaVideosFlow(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    ): Flow<List<VideoItem>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(getMediaVideo(selection, selectionArgs, sortOrder))
            }
        }
        applicationContext.contentResolver.registerContentObserver(VIDEO_COLLECTION_URI, true, observer)
        // initial value
        trySend(getMediaVideo(selection, selectionArgs, sortOrder))
        // close
        awaitClose { applicationContext.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getMediaVideo(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    ): List<VideoItem> {
        val videoItems = mutableListOf<VideoItem>()
        applicationContext.contentResolver.query(
            VIDEO_COLLECTION_URI,
            VIDEO_PROJECTION,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
            val dateModifiedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val absolutePath = cursor.getString(dataColumn)
                val name = absolutePath.split("/").lastOrNull().toString()

                // Format date in "date/month/year" format
                // Retrieve and format the date
                val dateTaken = cursor.getLong(dateTakenColumn)
                val dateModified = cursor.getLong(dateModifiedColumn)
                val date = when {
                    dateTaken > 0 -> dateFormat.format(dateTaken)
                    dateModified > 0 -> dateFormat.format(dateModified * 1000L) // dateModified is in seconds
                    else -> "Unknown" // Fallback if no date is available
                }
                val  uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                videoItems.add(
                    VideoItem(
                        id = id,
                        name = name,
                        absolutePath = absolutePath,
                        duration = cursor.getLong(durationColumn),
                        uri = ContentUris.withAppendedId(VIDEO_COLLECTION_URI, id),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        size = cursor.getLong(sizeColumn),
                        dateModified = cursor.getLong(dateModifiedColumn),
                        date = date,
                    )
                )
            }
        }
        return videoItems.filter { File(it.absolutePath).exists() }
    }












    companion object {

        const val TAG = "Local Media Provider"

        val VIDEO_COLLECTION_URI: Uri
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.YEAR
        )
    }

}

