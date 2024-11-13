package com.example.soraplayer.Player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.soraplayer.Data.LocalMediaProvider
import com.example.soraplayer.Model.MinimizedPlayerState
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import androidx.media3.extractor.DefaultExtractorsFactory


@UnstableApi
class PlayerViewModel(
    @SuppressLint("StaticFieldLeak") private val context: Context,
    val player: ExoPlayer,
    private val mediaSession: MediaSession,
    private val loudnessEnhancer: LoudnessEnhancer,
    private val listener: Player.Listener,
    private val localMediaProvider: LocalMediaProvider,
): ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    init {
        player.prepare()
        startPositionUpdater()
        player.playWhenReady = true

        // Ensure duration is updated once player is ready
        player.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _playerState.update {
                        it.copy(currentVideoItem = it.currentVideoItem?.copy(duration = player.duration))
                    }
                }
            }
        })
    }

    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (true) {
                updateCurrentPosition(player.currentPosition)
                delay(1000) // Update every second
            }
        }
    }

    fun setPlaybackPosition(playbackPosition: Long) {
        player.seekTo(playbackPosition.toInt().toLong())
    }

    private val _isRotationLocked = MutableLiveData(false) // Lock rotation state
    val isRotationLocked: LiveData<Boolean> get() = _isRotationLocked

    private val sharedPreferences = context.getSharedPreferences("player_prefs", Context.MODE_PRIVATE)

    fun saveLastPlayedVideo(videoItem: VideoItem) {
        sharedPreferences.edit().apply {
            putString("last_video_uri", videoItem.uri.toString())
            putString("last_video_name", videoItem.name)
            putLong("last_video_size", videoItem.size)
            putInt("last_video_width", videoItem.width)
            putInt("last_video_height", videoItem.height)
            putLong("last_video_duration", videoItem.duration)
            putLong("last_video_date_modified", videoItem.dateModified)
            apply()
        }
    }


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




      fun updateCurrentVideoItem(videoItem: VideoItem) {
        _playerState.update {
            it.copy(
                currentVideoItem = videoItem,
                lastPlayedVideoItem = videoItem
            )
        }

        setMediaItem(_playerState.value.currentVideoItem!!.uri)
          saveLastPlayedVideo(videoItem)
    }

    private fun updateCurrentPosition(position: Long) {
        _playerState.update { state ->
            state.copy(currentPosition = position)
        }
    }


    fun getLastPlayedVideoItem(): VideoItem? {
        return _playerState.value.lastPlayedVideoItem
    }



    private fun setMediaItem(uri: Uri) {
        player.apply {
            clearMediaItems()
            addMediaItem(MediaItem.fromUri(uri)) // Load the video from the given URI
            playWhenReady = true
            prepare()
        }
        // Differentiate between local and internet videos
        if (uri.scheme == "http" || uri.scheme == "https") {
            // For internet videos, use the last segment of the URI as the title
            val internetTitle = uri.lastPathSegment?.substringBeforeLast('.') ?: "Online Video"
            _playerState.update {
                it.copy(
                    currentVideoItem = VideoItem(
                        uri = uri,
                        name = internetTitle, // Set the title based on URI for internet videos
                        duration = player.duration // Update duration when available
                    ),
                    videoTitle = internetTitle,
                    totalDuration = player.duration
                )
            }
        } else {
            // For local videos, retrieve metadata using `localMediaProvider`
            viewModelScope.launch {
                val videoItem = localMediaProvider.getVideoItemFromContentUri(uri)
                if (videoItem != null) {
                    _playerState.update {
                        it.copy(
                            currentVideoItem = videoItem,
                            videoTitle = videoItem.name, // Set the title from VideoItem
                            totalDuration = videoItem.duration
                        )
                    }
                    saveLastPlayedVideo(videoItem)
                }
            }
        }
    }






    @SuppressLint("DefaultLocale")
    fun formatPosition(position: Long): String {
        val totalSeconds = position / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
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
    fun loadLastPlayedVideo() {
        val lastUri = sharedPreferences.getString("last_video_uri", null)
        val lastName = sharedPreferences.getString("last_video_name", null)
        val lastSize = sharedPreferences.getLong("last_video_size", 0L) // Add default values if needed
        val lastWidth = sharedPreferences.getInt("last_video_width", 0)
        val lastHeight = sharedPreferences.getInt("last_video_height", 0)
        val lastDuration = sharedPreferences.getLong("last_video_duration", 0L)
        val lastDateModified = sharedPreferences.getLong("last_video_date_modified", 0L)
        val lastDate = sharedPreferences.getLong("last_video_date", 0L)

        if (lastUri != null && lastName != null) {
            val videoItem = VideoItem(
                id = 0, // You can generate an ID or use a default value
                uri = Uri.parse(lastUri),
                size = lastSize,
                width = lastWidth,
                height = lastHeight,
                duration = lastDuration,
                dateModified = lastDateModified,
                date = lastDate.toString(),
                name = lastName,
                absolutePath = "",
                artWork = null
            )
            _playerState.update {
                it.copy(lastPlayedVideoItem = videoItem)
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

        _playerState.update {
            it.copy(isPlaying = true) // Update the state to indicate the player is playing
        }


    }

    fun updateOrientation(orientation: Int) {
        _playerState.update {
            it.copy(orientation = orientation)
        }
    }


    fun onNewIntent(uri:Uri){
        player.clearMediaItems()
        localMediaProvider.getVideoItemFromContentUri(uri)?.let{
            updateCurrentVideoItem(it)
        }
    }

    fun onIntentFromDeepLink(slug: String, timestamp: Int?) {
        val videoUri = Uri.parse("https://sora-player.web.app/play/$slug")
        setMediaItem(videoUri)
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

                val mediaSession = MediaSession.Builder(application,player)
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
                        Log.e(TAG, "Playback error: ${error.errorCodeName}")
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
    val lastPlayedVideoItem: VideoItem? = null,
    val currentPosition: Long = 0L,
    val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
    val videoTitle: String? = null,
    val totalDuration: Long = 0L
)