package com.example.soraplayer.MusicPlayer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.MainScreen.MainViewModel
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.MusicPlayer.MusicService.MusicService
import com.example.soraplayer.Player.PlayerActivity
import com.example.soraplayer.Player.PlayerActivity.Companion
import com.example.soraplayer.R
import com.example.soraplayer.ui.theme.SoraPlayerTheme

@UnstableApi
class MusicPlayerActivity : ComponentActivity() {

    private val musicPlayerViewModel by viewModels<MusicPlayerViewModel>(factoryProducer = { MusicPlayerViewModel.factory })
    private val position = intent?.getLongExtra("CURRENT_POSITION", 0L) ?: 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        handleIntent(intent)




        setContent {
            SoraPlayerTheme {
                MusicPlayerScreen(
                    viewModel = musicPlayerViewModel,
                    onBackClick = { finish(
                    ) },
                )
            }
        }
    }






    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent?) {
        val intentUri = intent?.data
        val position = intent?.getLongExtra("CURRENT_POSITION", 0L) ?: 0L

        // Handle deep link
        if (intentUri != null) {
            handleDeepLink(intentUri)
            handleAndroidAppLink(intentUri)
        }

        // Handle video sharing (URL or file)
        if (intent?.action == Intent.ACTION_SEND) {
            handleShareIntent(intent)
        }

        if(position > 0) {
            musicPlayerViewModel.setPlaybackPosition(position)

        }

    }

    private fun handleDeepLink(uri: Uri?) {
        if (uri == null) {
            Log.e(com.example.soraplayer.MusicPlayer.MusicPlayerActivity.TAG, "Received null URI in deep link")
            return
        }

        val slug = uri.getQueryParameter("slug")
        val timestamp = uri.getQueryParameter("timestamp")?.toIntOrNull()

        if (slug != null) {
            // If slug is present, handle it by passing to ViewModel
            musicPlayerViewModel.onIntentFromDeepLink(slug , timestamp)
        } else {
            // If no slug, assume it's a regular video URI and pass it
            musicPlayerViewModel.onIntent(uri)
        }
    }

    private  fun handleAndroidAppLink(uri: Uri?) {
        val audioUrl = uri?.getQueryParameter("audio_url")
        Log.d(TAG, "Extracted audio URL: $audioUrl")// Correctly extract the 'video_url' parameter

        if (audioUrl != null) {
            val audioUri = Uri.parse(audioUrl)
            musicPlayerViewModel.onIntent(audioUri) // Pass the video URL to the player
        } else {
            Log.e(com.example.soraplayer.MusicPlayer.MusicPlayerActivity.TAG, "Video URL is missing in the deep link")
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

                   }
               } else if (type?.startsWith("audio/") == true) {
                   // Handle local audio file
                   val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                   uri?.let {
                       musicPlayerViewModel.onIntent(it)  // Handle local audio file in ViewModel

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