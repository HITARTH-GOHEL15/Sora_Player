package com.example.soraplayer.Model

import android.net.Uri

data class MusicItem(
    val id: Long,
    val name: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri?,
    val absolutePath: String
)