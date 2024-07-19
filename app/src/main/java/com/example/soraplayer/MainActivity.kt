package com.example.soraplayer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soraplayer.Presentation.UI.VideoScreen.MediaContent
import com.example.soraplayer.Presentation.UI.VideoScreen.MediaFileViewModel
import com.example.soraplayer.Presentation.UI.VideoScreen.MediaList
import com.example.soraplayer.Presentation.UI.VideoScreen.MediaPermissionTextProvider
import com.example.soraplayer.Presentation.UI.VideoScreen.PermissionDialog
import com.example.soraplayer.Presentation.UI.VideoScreen.PermissionViewModel
import com.example.soraplayer.Presentation.UI.VideoScreen.RequestMediaPermission
import com.example.soraplayer.Presentation.UI.VideoScreen.VideoScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
               VideoScreen()
            }
//               val viewModel = viewModel<PermissionViewModel>()
//                val viewModel2 = viewModel<MediaFileViewModel>()
//                val dialoqQueue = viewModel.visiblePermissionDialogQueue
//
//                val mediaPermissionResultLauncher = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.RequestPermission(),
//                    onResult = { isGranted ->
//                        viewModel.onPermissionResult(
//                            isGranted = isGranted,
//                            permission = Manifest.permission.READ_MEDIA_VIDEO
//                        )
//
//                    }
//                )
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Button(onClick = {
//                        mediaPermissionResultLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
//                    }) {
//                        Text(text = "Request one permission")
//                    }
//                }
//                dialoqQueue
//                    .reversed()
//                    .forEach { permission ->
//                    PermissionDialog(
//                        permissionTextProvider =  when(permission) {
//                            Manifest.permission.READ_MEDIA_VIDEO -> {
//                                MediaPermissionTextProvider()
//                            }
//
//                            else -> return@forEach
//                        },
//                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
//                            permission
//                        ) ,
//                        onDismiss = viewModel::dismissDialog,
//                        onOkClick = {
//                            viewModel.dismissDialog()
//
//                            arrayOf(
//                                permission
//                            )
//
//                                    },
//                        onGoToAppSettingsClick = ::openAppSetting
//                    )
//
//                }
            }
        }
    }


fun Activity.openAppSetting() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}






