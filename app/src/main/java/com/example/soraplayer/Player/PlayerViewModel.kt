package com.example.soraplayer.Player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.soraplayer.Data.LocalMediaProvider
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.MyApplication
import com.example.soraplayer.Player.PlayerActivity.Companion.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import android.provider.Settings

@UnstableApi
class PlayerViewModel(
    @SuppressLint("StaticFieldLeak") val context: Context,
    val player: ExoPlayer,
    private val mediaSession: androidx.media3.session.MediaSession,
    private var loudnessEnhancer: LoudnessEnhancer,
    private val listener: Player.Listener,
    private val localMediaProvider: LocalMediaProvider,
): ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()
    // Subtitle toggle
    var brightness = mutableStateOf(0.5f)
    var volumeLevel = mutableStateOf(0.5f)

    init {
        player.addListener(listener)
        player.prepare().also {
            Log.d(TAG, "viewModel created and player is prepared")
        }
    }
    // Enhanced error handling in Player Listener
    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "Playback error: ${error.message}")
            if (error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED) {
                Log.e(TAG, "Source error: Check URI or network availability.")
            }
        }

        // Volume and audio session updates
        override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
            super.onDeviceVolumeChanged(volume, muted)
            player.volume = volume.toFloat()
        }

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            super.onAudioSessionIdChanged(audioSessionId)
            try {
                loudnessEnhancer.release()
                loudnessEnhancer = LoudnessEnhancer(audioSessionId)
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing LoudnessEnhancer: ${e.message}")
            }
        }
    }

    private val _isRotationLocked = MutableLiveData(false) // Lock rotation state
    val isRotationLocked: LiveData<Boolean> get() = _isRotationLocked

    // Function to toggle the lock rotation state
    fun toggleRotationLock() {
        _isRotationLocked.value = !(_isRotationLocked.value ?: false)
    }

    // New function to set custom orientation

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
                currentVideoItem = videoItem,
            )
        }
        setMediaItem(_playerState.value.currentVideoItem!!.uri)
    }



    private fun setMediaItem(uri: Uri) {
        Log.d(TAG, "Setting media item: $uri")
        if (uri.scheme == null || uri.host == null) {
            Log.e(TAG, "Invalid URI: $uri")
            return
        }// Log the URI
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

    fun updateOrientation(orientation: Int) {
        _playerState.update {
            it.copy(orientation = orientation)
        }
    }

    fun toggleSubtitles() {
        val trackSelector = player.trackSelector as DefaultTrackSelector
        player.trackSelectionParameters = trackSelector.parameters
        val hasSubtitles = trackSelector.currentMappedTrackInfo != null
        val parameters = if (hasSubtitles) {
            trackSelector.parameters.buildUpon().setRendererDisabled(C.TRACK_TYPE_TEXT, false).build()
        } else {
            trackSelector.parameters.buildUpon().setRendererDisabled(C.TRACK_TYPE_TEXT, true).build()
        }
        trackSelector.parameters = parameters
    }

    fun onIntent(uri: Uri) {
            // Check if the URI is a web URL (http/https) or a local file (content/file)
        // Check if the URI is a web URL (http/https) or a local file (content/file)
        Log.d(TAG, "Received URI intent: $uri")
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
        Log.d(TAG, "Attempting to play media from: $mediaUri")
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
                    .setBufferDurationsMs(
                        32_000, 64_000, 1_000, 5_000
                    )
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

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e(TAG, "Playback error: ${error.message}")
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
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
)