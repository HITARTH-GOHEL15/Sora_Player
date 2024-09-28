package com.example.soraplayer.Player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.soraplayer.Data.LocalMediaProvider
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

@UnstableApi
class PlayerViewModel(
    @SuppressLint("StaticFieldLeak") private val context: Context,
    val player: ExoPlayer,
    private val mediaSession: androidx.media3.session.MediaSession,
    private val loudnessEnhancer: LoudnessEnhancer,
    private val listener: Player.Listener,
    private val localMediaProvider: LocalMediaProvider,
): ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    init {
        player.prepare().also {
            Log.d(TAG, "viewModel created and player is prepared")
        }
    }

    override fun onCleared() {
        player.removeListener(listener)
        player.release()
        mediaSession.release()
        loudnessEnhancer.release()
        Log.d(TAG, "on Cleared called and player is released")
        super.onCleared()
    }

    private fun updateCurrentVideoItem(videoItem: VideoItem){
        _playerState.update {
            it.copy(
                currentVideoItem = videoItem
            )
        }
        setMediaItem(_playerState.value.currentVideoItem!!.uri)
    }

    private fun setMediaItem(uri: Uri) {
        player.apply {
            clearMediaItems()
            addMediaItem(MediaItem.fromUri(uri)) // Load the video from the given URI
            playWhenReady = true
            prepare()

            _playerState.update {
                it.copy(isPlaying = true) // Update the state to indicate the player is playing
            }
        }
    }

    fun onPlayPauseClick(){
        if (player.isPlaying){
            player.pause().also {
                _playerState.update {
                    it.copy(isPlaying = false)
                }
            }
        }else {
            player.play().also {
                _playerState.update {
                    it.copy(isPlaying = true)
                }
            }
        }
    }

    fun playPauseOnActivityLifeCycleEvents(shouldPause: Boolean){
        if(player.isPlaying && shouldPause){
            player.pause().also{
                _playerState.update { it.copy(isPlaying = false) }
            }
        }else if(!player.isPlaying && !shouldPause){
            player.play().also {
                _playerState.update { it.copy(isPlaying = true) }
            }
        }
    }

    fun onRotateScreen(){
        val orientation = if(_playerState.value.orientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE){
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        }else{
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        }
        _playerState.update {
            it.copy(orientation = orientation)
        }
    }

    fun onResizeClick(){
        _playerState.update {
            it.copy(
                resizeMode = if(_playerState.value.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT){
                    AspectRatioFrameLayout.RESIZE_MODE_FILL
                }else{
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            )
        }
    }

    fun onIntent(uri: Uri) {
            // Check if the URI is a web URL (http/https) or a local file (content/file)
        // Check if the URI is a web URL (http/https) or a local file (content/file)
        if (uri.scheme == "http" || uri.scheme == "https") {
            // It's an internet URL, set the media item directly
            setMediaItem(uri)
        } else {
            // It's a local video file, handle it with the localMediaProvider (already in your code)
            localMediaProvider.getVideoItemFromContentUri(uri)?.let {
                updateCurrentVideoItem(it)


            }
        }


    }

    fun onNewIntent(uri:Uri){
        player.clearMediaItems()
        localMediaProvider.getVideoItemFromContentUri(uri)?.let{
            updateCurrentVideoItem(it)
        }
    }

    fun onIntentFromDeepLink(slug: String, timestamp: Int?) {
        val mediaUri = Uri.parse("https://sora-player.web.app/play/$slug")
        setMediaItem(mediaUri)
        timestamp?.let {
            player.seekTo(it * 1000L)
        }

    }





    companion object{

        const val TAG = "PlayerViewModel"

        val factory = viewModelFactory {

            initializer {

                val application = (this[APPLICATION_KEY] as MyApplication)
                val context = application.applicationContext

                val renderersFactory = DefaultRenderersFactory(context)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

                val loadControl = DefaultLoadControl.Builder()
                    .setBufferDurationsMs(32_000, 64_000, 1_000, 5_000)
                    .build()

                val trackSelector = DefaultTrackSelector(context).apply {
                    parameters = buildUponParameters().setMaxVideoSizeSd().build()
                }

                val player = ExoPlayer.Builder(context, renderersFactory)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                            .build(),
                        true
                    )
                    .build()

                val mediaSession = androidx.media3.session.MediaSession.Builder(application,player)
                    .setId(UUID.randomUUID().toString())
                    .build()

                var loudnessEnhancer = LoudnessEnhancer(player.audioSessionId)

                val listener = object: Player.Listener{
                    override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                        super.onDeviceVolumeChanged(volume, muted)
                        player.volume = volume.toFloat()
                    }

                    override fun onAudioSessionIdChanged(audioSessionId: Int) {
                        super.onAudioSessionIdChanged(audioSessionId)
                        loudnessEnhancer.release()

                        try {
                            loudnessEnhancer = LoudnessEnhancer(audioSessionId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                player.addListener(listener)

                PlayerViewModel(
                    context = context,
                    player = player,
                    mediaSession = mediaSession,
                    loudnessEnhancer = loudnessEnhancer,
                    listener = listener,
                    localMediaProvider = application.container.localMediaProvider
                )
            }

        }
    }
}

@UnstableApi
data class PlayerState(
    val isPlaying: Boolean = false,
    val currentVideoItem: VideoItem? = null,
    val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
)