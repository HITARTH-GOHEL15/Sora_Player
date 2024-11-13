package com.example.soraplayer.Model

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri

data class VideoItem(
    val name: String,
    val absolutePath: String = "",
    val id: Long = 0L,
    val uri: Uri,
    val size: Long = 0L,
    val width: Int = 0,
    val height: Int = 0,
    val duration: Long = 0L,
    val dateModified: Long = 0L,
    val artWork: Bitmap? = null,
    val date: String = ""
){


    @SuppressLint("DefaultLocale")
    fun Long.toHhMmSs(): String {
        val seconds = (this / 1000).toInt()
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }
}

data class FolderItem(
    val name: String,
    val videoItems: List<VideoItem>
)

data class MinimizedPlayerState(
    val isPlaying: Boolean = false,
    val isMusic: Boolean = true, // True for music, false for video
    val lastPlayedUri: Uri? = null,
    val title: String = "",
    val thumbnail: Bitmap? = null
)





