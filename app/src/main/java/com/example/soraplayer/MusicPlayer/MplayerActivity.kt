package com.example.soraplayer.MusicPlayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.Player.PlayerActivity
import com.example.soraplayer.ui.theme.SoraPlayerTheme

@UnstableApi
class MusicPlayerActivity : ComponentActivity() {

    private val musicPlayerViewModel by viewModels<MusicPlayerViewModel>(factoryProducer = { MusicPlayerViewModel.factory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleShareIntent(intent)
        val audioUri = intent.data
        if (audioUri != null) {
            Log.d(TAG, "OnNewIntent Uri is not null")
            musicPlayerViewModel.onNewIntent(audioUri)
            startMusicService(audioUri)
            stopMusicService()
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

    private fun startMusicService(audioUri: Uri) {
        val intent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY
            putExtra(MusicService.EXTRA_TRACK, audioUri.toString())
        }
        startService(intent)
    }
    private fun stopMusicService() {
        val intent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_STOP
        }
        startService(intent)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
        val audioUri = intent.data
        if (audioUri != null) {
            Log.d(TAG, "OnNewIntent Uri is not null")
            musicPlayerViewModel.onNewIntent(audioUri)
            startMusicService(audioUri)
            stopMusicService()
        }
    }

   private fun handleShareIntent(intent: Intent?) {
       when (intent?.action) {
           Intent.ACTION_SEND -> {
               val type = intent.type
               if (type == "text/plain") {
                   // Handle shared URL (for internet music links)
                   val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                   sharedText?.let {
                       val uri = Uri.parse(it)
                       musicPlayerViewModel.onIntent(uri)  // Handle URL in ViewModel
                       startMusicService(uri)
                   }
               } else if (type?.startsWith("audio/") == true) {
                   // Handle local audio file
                   val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                   uri?.let {
                       musicPlayerViewModel.onIntent(it)  // Handle local audio file in ViewModel
                       startMusicService(uri)
                   }
               }
           }
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