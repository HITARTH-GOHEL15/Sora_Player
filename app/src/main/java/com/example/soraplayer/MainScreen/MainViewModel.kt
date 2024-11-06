package com.example.soraplayer.MainScreen

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
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
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.Data.LocalMusicProvider
import com.example.soraplayer.Player.PlayerActivity.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
   private val  localMediaProvider: LocalMediaProvider,
    private val localMusicProvider: LocalMusicProvider
): ViewModel() {

    private var pendingRenameUri: Uri? = null
    private var pendingNewName: String? = null
    private var pendingDeleteUri: Uri? = null

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

    private val _folderItemStateFlow =  _videoItemsStateFlow.map{ videoItemsList ->

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









    @RequiresApi(Build.VERSION_CODES.Q)
    fun handleActivityResult(requestCode: Int, resultCode: Int, context: Context) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1002 -> retryDeleteOperation(context)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteVideo(context: Context, videoUri: Uri) {
        viewModelScope.launch {
            pendingDeleteUri = videoUri
            try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.delete(videoUri, null, null)
                }
            } catch (e: RecoverableSecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val intentSender: IntentSender = e.userAction.actionIntent.intentSender
                    (context as? Activity)?.startIntentSenderForResult(
                        intentSender,
                        1002,
                        null,
                        0,
                        0,
                        0
                    )
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    // Retry delete after permission granted
    @RequiresApi(Build.VERSION_CODES.Q)
    fun retryDeleteOperation(context: Context) {
        pendingDeleteUri?.let { uri ->
            deleteVideo(context, uri)
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

    private val _isGridLayout = MutableStateFlow(true) // Default to grid layout
    val isGridLayout: StateFlow<Boolean> get() = _isGridLayout


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