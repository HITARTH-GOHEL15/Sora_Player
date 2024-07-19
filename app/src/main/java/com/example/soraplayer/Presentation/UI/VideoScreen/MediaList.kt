package com.example.soraplayer.Presentation.UI.VideoScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.soraplayer.Data.MediaFile

@Composable
fun MediaList(
    mediaFiles: List<MediaFile>
) {
    LazyColumn {
        items(mediaFiles.size) {  index ->
            Text(mediaFiles[index].name)
        }
    }
}