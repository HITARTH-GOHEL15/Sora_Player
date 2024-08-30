package com.example.soraplayer.MainScreen

import com.example.soraplayer.Data.LocalMediaProvider
import com.example.soraplayer.Model.FolderItem
import com.example.soraplayer.MyApplication
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.soraplayer.Data.LocalMusicProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
   private val  localMediaProvider: LocalMediaProvider,
    private val localMusicProvider: LocalMusicProvider
): ViewModel() {

    private val _videoItemsStateFlow = localMediaProvider.getMediaVideosFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _musicItemsStateFlow = localMusicProvider.getMediaMusicFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _folderItemStateFlow = _videoItemsStateFlow.map{ videoItemsList ->

        videoItemsList.map { videoItem ->
            val splitPath = videoItem.absolutePath.split("/")
            val folderName = splitPath[splitPath.size - 2]
            FolderItem(
                name = folderName,
                videoItemsList.filter {
                    val splitPathStrings = it.absolutePath.split("/")
                    val name = splitPathStrings[splitPathStrings.size - 2]
                    folderName == name
                }
            )
        }.distinct()

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videoItemsStateFlow = _videoItemsStateFlow

    val folderItemStateFlow = _folderItemStateFlow

    val musicItemsStateFlow = _musicItemsStateFlow

    var currentSelectedFolder by mutableStateOf(
        FolderItem("null", emptyList())
    )

    fun updateCurrentSelectedFolderItem(folderItem: FolderItem){
        currentSelectedFolder = folderItem
    }
    private val _isRefreshing = MutableStateFlow(false)
    var isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        loadVideos()
        loadFolders()
    }

    private fun loadVideos() {

    }

    private fun loadFolders() {

    }

 fun renameVideo(id: Long , newName: String) {
      _videoItemsStateFlow.value.map {
         if (
             it.id == id
         ) {
             it.copy(name = newName)
         } else {
             it
         }
     }
 }

    fun removeVideo(id: Long) {
        _videoItemsStateFlow.value.filterNot {
            it.id == id
        }
    }



    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(2000)
            loadVideos()
            loadFolders()
            _isRefreshing.value = false
        }
    }


    companion object{
        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyApplication)
                MainViewModel(
                    localMediaProvider = application.container.localMediaProvider,
                    localMusicProvider = application.container.localMusicProvider
                )
            }
        }

    }
}