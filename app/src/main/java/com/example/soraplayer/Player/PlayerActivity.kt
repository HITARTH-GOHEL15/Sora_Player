package com.example.soraplayer.Player

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.ui.theme.SoraPlayerTheme

@UnstableApi
class PlayerActivity: ComponentActivity() {

    private val playerViewModel by viewModels<PlayerViewModel>(factoryProducer = { PlayerViewModel.factory })

    override fun onCreate(savedInstanceState: Bundle?) {
        handleWindowInsetsAndDecors(window = window)
        super.onCreate(savedInstanceState)

        requestedOrientation = playerViewModel.playerState.value.orientation

        handleIntent(intent)




        setContent {
            SoraPlayerTheme(
                darkTheme = !isSystemInDarkTheme()
            ) {
                Surface(Modifier.fillMaxSize()) {
                    PlayerScreen(
                        viewModel = playerViewModel,
                        onRotateScreenClick = {
                            playerViewModel.onRotateScreen()
                            requestedOrientation = playerViewModel.playerState.value.orientation
                        },
                        onBackClick = { finish() }
                    )
                }
            }

        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent?) {
        val intentUri = intent?.data

        // Handle deep link
        if (intentUri != null) {
            handleDeepLink(intentUri)
        }

        // Handle video sharing (URL or file)
        if (intent?.action == Intent.ACTION_SEND) {
            handleShareIntent(intent)
        }
    }

    private fun handleDeepLink(uri: Uri?){
        val slug = uri?.getQueryParameter("slug")
        val timestamp = uri?.getQueryParameter("timestamp")?.toIntOrNull()

        if (uri != null) {
            Log.d(TAG , "handle URI deep link")
            playerViewModel.onIntent(uri)
        } else if(slug != null) {
           playerViewModel.onIntentFromDeepLink(slug, timestamp)
        }

    }

    private fun handleShareIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val type = intent.type
                if (type == "text/plain") {
                    // For internet video URLs shared as text
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    sharedText?.let {
                        val uri = Uri.parse(it)
                        playerViewModel.onIntent(uri)
                    }
                } else if (type?.startsWith("video/") == true) {
                    // For local video files
                    val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    uri?.let {
                        playerViewModel.onIntent(it)
                    }
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                val uriList = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                uriList?.let {
                    for (uri in it) {
                        playerViewModel.onIntent(uri)
                        break // only handle the first video
                    }
                }
            }
        }
    }


    override fun onPause() {
        playerViewModel.playPauseOnActivityLifeCycleEvents(shouldPause = true)
        super.onPause()
    }

    override fun onResume() {
        playerViewModel.playPauseOnActivityLifeCycleEvents(shouldPause = false)
        super.onResume()
    }

    companion object {

        const val TAG = "PlayerActivity"

        fun handleWindowInsetsAndDecors(window: Window) {

            WindowCompat.setDecorFitsSystemWindows(window, false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        }

    }
}