package com.example.soraplayer.MusicPlayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.soraplayer.Data.LocalMusicProvider
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.MusicPlayer.MusicService.MusicService
import com.example.soraplayer.MusicPlayer.MusicService.MusicServiceHandler
import com.example.soraplayer.MyApplication
import com.example.soraplayer.Utils.MediaStateEvents
import com.example.soraplayer.Utils.MusicStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi
class MusicPlayerViewModel(
    private val context: Context,
    val player: ExoPlayer,
    private val musicServiceHandler: MusicServiceHandler,
    private val localMusicProvider: LocalMusicProvider,
) : ViewModel() {

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState())
    val musicPlayerState = _musicPlayerState.asStateFlow()


    init {
        player.prepare()
        observeMusicStates()
        fetchMusic()
        startPositionUpdater()
    }

    @SuppressLint("DefaultLocale")
    fun formatPosition(position: Long): String {
        val totalSeconds = position / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    // Function to start a coroutine that updates current position at regular intervals
    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (true) {
                updateCurrentPosition(player.currentPosition)
                delay(1000) // Update every second
            }
        }
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }

    private fun fetchMusic() {
        viewModelScope.launch {
            localMusicProvider.getMediaMusicFlow().collectLatest { musicItems ->
                // Automatically update the current track to the first one in the list
                _musicPlayerState.update { state ->
                    state.copy(musicItems = musicItems)
                }
            }
        }
    }









    fun updateCurrentTrack(trackItem: MusicItem) {
        _musicPlayerState.update { state ->
            state.copy(
                currentTrack = trackItem,
                currentTrackIndex = state.musicItems.indexOf(trackItem),
                duration = player.duration,
                currentPosition = player.currentPosition,
                isPlaying = true
            )
        }
        setMediaItem(trackItem.uri)

        startForegroundService()

    }

    // Update position in the view model state whenever the player position changes
    fun updateCurrentPosition(position: Long) {
        _musicPlayerState.update { state ->
            state.copy(currentPosition = position)
        }
    }


    fun setMediaItem(uri: Uri) {
        player.apply {
            clearMediaItems()
            addMediaItem(MediaItem.fromUri(uri))
            duration
            playWhenReady = true
            prepare()
        }
        _musicPlayerState.update { state ->
            state.copy(isPlaying = true)
        }
    }

    private fun observeMusicStates() {
        viewModelScope.launch {
            musicServiceHandler.musicStates.collectLatest { musicState ->
                when (musicState) {
                    is MusicStates.Initial -> {
                        // Handle initial state if necessary
                    }
                    is MusicStates.MediaBuffering -> {
                        _musicPlayerState.update { state ->
                            state.copy(
                                isBuffering = true,
                                currentPosition = musicState.progress
                            )
                        }
                    }
                    is MusicStates.MediaReady -> {

                        _musicPlayerState.update { state ->
                            state.copy(
                                isBuffering = false,
                            )
                        }
                    }
                    is MusicStates.MediaPlaying -> {
                        _musicPlayerState.update { state ->
                            state.copy(isPlaying = musicState.isPlaying)
                        }

                    }
                    is MusicStates.CurrentMediaPlaying -> {
                        // Update current track index or related UI
                        _musicPlayerState.update { state ->
                            state.copy(currentTrackIndex = musicState.mediaItemIndex)
                        }
                    }
                    is MusicStates.MediaProgress -> {
                        _musicPlayerState.update { state ->
                            state.copy(currentPosition = musicState.progress)
                        }
                    }
                    else -> {
                        // Optionally handle unexpected states
                        Log.w(TAG, "Unhandled MusicState: $musicState")
                    }
                }
            }
        }
    }

    fun startForegroundService() {
        val serviceIntent = Intent(context, MusicService::class.java).apply {
            action = "ACTION_START_FOREGROUND"
        }
        context.startService(serviceIntent)
    }



    fun onPlayPauseClick() {
       viewModelScope.launch {
           musicServiceHandler.onMediaStateEvents(MediaStateEvents.PlayPause)
       }
    }

    fun onSeekForwardClick() {
        viewModelScope.launch {
            musicServiceHandler.onMediaStateEvents(MediaStateEvents.Forward)
        }
    }

    fun onSeekBackwardClick() {
        viewModelScope.launch {
            musicServiceHandler.onMediaStateEvents(MediaStateEvents.Backward)
        }
    }


    fun playPauseOnActivityLifeCycleEvents(shouldPause: Boolean) {
        if (shouldPause) {
            viewModelScope.launch {
                musicServiceHandler.onMediaStateEvents(MediaStateEvents.Stop)
            }
        } else {
            // No need to stop the music when app is backgrounded
            startForegroundService()
        }
    }


    fun onIntent(uri: Uri) {
        // Check if it's a local music file or a URL
        // Check if the URI is a web URL (http/https) or a local file (content/file)
        // Check if the URI is a web URL (http/https) or a local file (content/file)
        if (uri.scheme == "http" || uri.scheme == "https") {
            // It's an internet URL, set the media item directly
            setMediaItem(uri)
        } else {
            // It's a local video file, handle it with the localMediaProvider (already in your code)
            localMusicProvider.getMusicItemFromUri(uri)?.let {
                updateCurrentTrack(it)
            }
        }
    }

    fun onNewIntent(uri: Uri) {
        player.clearMediaItems()
        localMusicProvider.getMusicItemFromUri(uri)?.let {
            updateCurrentTrack(it)
        }
    }

    fun onIntentFromDeepLink(slug: String, timestamp: Int?) {
        val audioUri = Uri.parse("https://sora-player.web.app/play-music/$slug")
        setMediaItem(audioUri)
        timestamp?.let {
            player.seekTo(it * 1000L)
        }
    }

    fun setPlaybackPosition(playbackPosition: Long) {
        player.seekTo(playbackPosition.toInt().toLong())
    }


    companion object {
        const val TAG = "MusicPlayerViewModel"

        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyApplication)
                val context = application.applicationContext

                val player = ExoPlayer.Builder(context)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                            .build(),
                        true
                    )
                    .build()

                val musicServiceHandler = MusicServiceHandler(player)

                MusicPlayerViewModel(
                    context = context,
                    player = player,
                    localMusicProvider = LocalMusicProvider(application),
                    musicServiceHandler = musicServiceHandler
                )
            }
        }
    }
}





data class MusicPlayerState(
    val musicItems: List<MusicItem> = emptyList(),
    val currentTrack: MusicItem? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val currentTrackIndex: Int = -1,
    val artwork: Bitmap? = null  // Add this field to store the artwork URI
)