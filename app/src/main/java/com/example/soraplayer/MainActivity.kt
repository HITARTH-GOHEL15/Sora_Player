package com.example.soraplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soraplayer.MainScreen.MainScreen
import com.example.soraplayer.MainScreen.MainViewModel
import com.example.soraplayer.MusicPlayer.MusicPlayerActivity
import com.example.soraplayer.Player.PlayerActivity
import com.example.soraplayer.Presentation.Common.RequestMediaPermission
import com.example.soraplayer.ui.theme.SoraPlayerTheme
import dagger.hilt.android.UnstableApi


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>(factoryProducer = { MainViewModel.factory })


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
                    color = Color(0xFF222831)
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
                            },
                            onMusicItemClick = { musicItem ->
                                val playerIntent =
                                    Intent(
                                        this@MainActivity,
                                        MusicPlayerActivity::class.java
                                    ).apply {
                                        data = musicItem.uri
                                    }
                                playerActivityLauncher.launch(playerIntent)
                            },
                            onPlayStream = { url ->
                                // Trigger PlayerActivity with the video URL
                                val intent = Intent(this, PlayerActivity::class.java).apply {
                                    data = Uri.parse(url)  // Pass the URL to PlayerActivity
                                }
                                startActivity(intent)
                            },
                        )
                    }

                }

            }

        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityResult(requestCode, resultCode, this)
    }


}















