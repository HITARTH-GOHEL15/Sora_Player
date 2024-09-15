package com.example.soraplayer.MusicPlayer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.soraplayer.Data.LocalMusicProvider
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@UnstableApi
class MusicPlayerViewModel(
    private val context: Context,
    val player: ExoPlayer,
    private val mediaSession: MediaSession,
    private val localMusicProvider: LocalMusicProvider,
) : ViewModel() {

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState())
    val musicPlayerState = _musicPlayerState.asStateFlow()

    init {
        player.prepare()
        fetchMusic()
    }

    override fun onCleared() {
        player.release()
        mediaSession.release()
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

    private fun updateCurrentTrack(trackItem: MusicItem) {
        _musicPlayerState.update { state ->
            state.copy(currentTrack = trackItem)
        }
        setMediaItem(trackItem.uri)
    }

    private fun setMediaItem(uri: Uri) {
        player.apply {
            clearMediaItems()
            addMediaItem(MediaItem.fromUri(uri))  // Load the media item from the given URI
            playWhenReady = true
            prepare()
        }
        _musicPlayerState.update { state ->
            state.copy(isPlaying = true)
        }
    }

    fun onPlayPauseClick() {
        if (player.isPlaying) {
            player.pause()
            _musicPlayerState.update { state -> state.copy(isPlaying = false) }
            sendMusicServiceCommand(MusicService.ACTION_PAUSE)
        } else {
            player.play()
            _musicPlayerState.update { state -> state.copy(isPlaying = true) }
            sendMusicServiceCommand(MusicService.ACTION_PLAY)
        }
    }

    fun onSeekForwardClick() {
        player.seekForward()
        sendMusicServiceCommand(MusicService.ACTION_SEEK_FORWARD)
    }

    fun onSeekBackwardClick() {
        player.seekBack()
        sendMusicServiceCommand(MusicService.ACTION_SEEK_BACKWARD)
    }

    fun playPauseOnActivityLifeCycleEvents(shouldPause: Boolean) {
        if (shouldPause) {
            player.pause()
            _musicPlayerState.update { state -> state.copy(isPlaying = false) }
        } else {
            player.play()
            _musicPlayerState.update { state -> state.copy(isPlaying = true) }
        }
    }

    fun onIntent(uri: Uri) {
        // Check if it's a local music file or a URL
        if (uri.scheme == "http" || uri.scheme == "https") {
            // It's an internet URL, directly set the media item
            setMediaItem(uri)
        } else {
            // Try to fetch a local music item from the URI
            localMusicProvider.getMusicItemFromUri(uri)?.let {
                updateCurrentTrack(it)
            } ?: run {
                // If it's not in the local library, try to play it directly
                setMediaItem(uri)
            }
        }
    }

    fun onNewIntent(uri: Uri) {
        player.clearMediaItems()
        onIntent(uri)
    }


    private fun sendMusicServiceCommand(action: String, uri: Uri? = null) {
        val intent = Intent(context, MusicService::class.java).apply {
            this.action = action
            uri?.let { putExtra(MusicService.EXTRA_TRACK, it.toString()) }
        }
        context.startService(intent)
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

                val mediaSession = MediaSession.Builder(context, player).build()

                MusicPlayerViewModel(
                    context = context,
                    player = player,
                    mediaSession = mediaSession,
                    localMusicProvider = LocalMusicProvider(application)
                )
            }
        }
    }
}

data class MusicPlayerState(
    val musicItems: List<MusicItem> = emptyList(),
    val currentTrack: MusicItem? = null,
    val isPlaying: Boolean = false
)