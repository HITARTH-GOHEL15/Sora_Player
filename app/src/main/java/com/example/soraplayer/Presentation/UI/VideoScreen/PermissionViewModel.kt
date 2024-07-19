package com.example.soraplayer.Presentation.UI.VideoScreen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel: ViewModel() {

//    // [READ_EXTERNAL_STORAGE]
//    val visiblePermissionDialogQueue = mutableStateListOf<String>()
//
//    fun dismissDialog() {
//        visiblePermissionDialogQueue.removeAt(visiblePermissionDialogQueue.lastIndex)
//    }
//
//    fun onPermissionResult(
//        permission: String, isGranted: Boolean
//    ) {
//        if(!isGranted){
//            visiblePermissionDialogQueue.add(0,permission)
//        }
//    }

    private val _hasMediaPermission = MutableLiveData(false)
    val hasMediaPermission: LiveData<Boolean> get() = _hasMediaPermission

    fun setPermissionGranted(granted: Boolean) {
        _hasMediaPermission.value = granted
    }
}