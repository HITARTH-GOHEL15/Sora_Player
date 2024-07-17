package com.example.soraplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.soraplayer.Presentation.UI.VideoScreen.VideoScreen
import com.example.soraplayer.ui.theme.SoraPlayerTheme
import com.example.soraplayer.vlc_inittialization.VLCManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        VLCManager.initialize(this)
        setContent {
          SoraPlayerTheme {
              Surface(
                  modifier = Modifier.fillMaxSize(),
                  color = MaterialTheme.colorScheme.background
              ) {
                VideoScreen()
              }
          }
        }
            }
        }


