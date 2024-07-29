package com.example.soraplayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.soraplayer.Player.PlayerActivity
import com.example.soraplayer.Presentation.Common.RequestMediaPermission
import com.example.soraplayer.MainScreen.MainScreen
import com.example.soraplayer.ui.theme.SoraPlayerTheme
import dagger.hilt.android.UnstableApi


class MainActivity : ComponentActivity() {


    @OptIn(androidx.media3.common.util.UnstableApi::class)
    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoraPlayerTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.background
                ) {
                    val playerActivityLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {}
                    )
                    RequestMediaPermission {
                        MainScreen(
                            onVideoItemClick = { videoItem ->
                                val playerIntent =
                                    Intent(this@MainActivity, PlayerActivity::class.java).apply {
                                        data = videoItem.uri
                                    }
                                playerActivityLauncher.launch(playerIntent)
                            }
                        )
                    }

                }
            }
        }
    }
}









