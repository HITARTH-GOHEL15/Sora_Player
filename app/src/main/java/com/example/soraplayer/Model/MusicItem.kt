package com.example.soraplayer.Model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class MusicItem(
    val id: Long,
    val name: String,
    val artist: String,
    val album: String,
    var duration: Long,
    val uri: Uri,
    val dateModified: Long,
    val size: Long,
    val absolutePath: String,
    val artWork: Bitmap?
)