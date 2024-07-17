package com.example.soraplayer.Presentation.UI.VideoScreen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestScreen(
    permission: String
) {
    // Permission request screen content
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {  granted ->
            permissionState.launchPermissionRequest()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Button(
        onClick = {
            launcher.launch(permission)
        }
    ) {
        Text(text = "Request Permission")
    }

    LaunchedEffect(
        permissionState.status
    ) {
        if(permissionState.status.isGranted){
            "Permission granted"
            // permission granted
            // do something
        } else {
            val shouldShowRationale = permissionState.status.shouldShowRationale
            if (shouldShowRationale) {
                snackbarHostState.showSnackbar(
                    message = "the app needs Storage Permission."
                )
            } else {
                snackbarHostState.showSnackbar(
                    message = "Permission denied. please go to Settings and enable permission",
                    actionLabel = "Settings",
                )
            }
        }
    }
    SnackbarHost(snackbarHostState)
}