package com.example.soraplayer.Presentation.UI.VideoScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soraplayer.Data.MediaFile
import com.example.soraplayer.Repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaFileViewModel @Inject constructor(private val repository: MediaRepository) : ViewModel() {
    private val _mediaFiles = MutableStateFlow<List<MediaFile>>(emptyList())
    val mediaFiles: StateFlow<List<MediaFile>> = _mediaFiles

    init {
        loadMediaFiles()
    }

    private fun loadMediaFiles() {
       viewModelScope.launch {
           _mediaFiles.value = repository.getMediaFiles()
       }
    }

}