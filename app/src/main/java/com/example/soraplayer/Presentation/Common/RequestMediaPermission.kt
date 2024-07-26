package com.example.soraplayer.Presentation.Common

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.soraplayer.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RequestMediaPermission(
    appContent: @Composable () -> Unit,
) {

            val readVideoPermissionState =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    rememberPermissionState(
                        android.Manifest.permission.READ_MEDIA_VIDEO
                    )
                } else {
                    rememberPermissionState(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }

            fun requestPermissions() {
                readVideoPermissionState.launchPermissionRequest()
            }

            LaunchedEffect(key1 = Unit) {
                if (!readVideoPermissionState.status.isGranted) {
                    requestPermissions()
                }
            }

            if (readVideoPermissionState.status.isGranted) {

                appContent()

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painterResource(id = R.drawable.warning_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "permission denied",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    if (readVideoPermissionState.status.shouldShowRationale) {
                        Spacer(modifier = Modifier.size(8.dp))
                        OutlinedButton(
                            onClick = { requestPermissions() },
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = "request Again!",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

