package com.example.soraplayer.MusicPlayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.ui.theme.SoraPlayerTheme

@UnstableApi
class MusicPlayerActivity : ComponentActivity() {

    private val musicPlayerViewModel by viewModels<MusicPlayerViewModel>(factoryProducer = { MusicPlayerViewModel.factory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioUri = intent.data
        if (audioUri != null) {
            Log.d(TAG, "Intent Uri is not null")
            musicPlayerViewModel.onIntent(audioUri)
        }

        setContent {
            SoraPlayerTheme {
                MusicPlayerScreen(
                    viewModel = musicPlayerViewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val audioUri = intent.data
        if (audioUri != null) {
            Log.d(TAG, "OnNewIntent Uri is not null")
            musicPlayerViewModel.onNewIntent(audioUri)
        }
    }

    override fun onPause() {
        musicPlayerViewModel.playPauseOnActivityLifeCycleEvents(shouldPause = true)
        super.onPause()
    }

    override fun onResume() {
        musicPlayerViewModel.playPauseOnActivityLifeCycleEvents(shouldPause = false)
        super.onResume()
    }

    companion object {
        const val TAG = "MusicPlayerActivity"
    }
}
