package com.example.soraplayer.Presentation.UI.VideoScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MediaContent(viewModel: MediaFileViewModel) {
    val mediaFile by viewModel.mediaFiles.collectAsState()
    
    // Do something with the media file
    Column {
        MediaList(mediaFiles = mediaFile)
    }
}