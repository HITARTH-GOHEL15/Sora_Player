package com.example.soraplayer.Player

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar
import com.example.soraplayer.MainScreen.MainScreen
import com.example.soraplayer.R
import com.example.soraplayer.Utils.toHhMmSs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onRotateScreenClick: () -> Unit,
    activity: ComponentActivity,
    onBackClick: () -> Unit,
) {
    val playerState by viewModel.playerState.collectAsState()
    var showControls by remember { mutableStateOf(false) }
    var isMinimized by remember { mutableStateOf(false) }
    var miniPlayerOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    // Volume and Brightness States
    val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    var volumeLevel by remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()) }
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
    var brightness by remember { mutableFloatStateOf(activity.window.attributes.screenBrightness.coerceIn(0f, 1f)) }

    // Show UI feedback for volume and brightness adjustments
    var showVolumeFeedback by remember { mutableStateOf(false) }
    var showBrightnessFeedback by remember { mutableStateOf(false) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000) // Hide controls after 3 seconds
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        if (change.position.x < screenWidth.toPx() / 2) {
                            // Left Side Gesture: Volume Control
                            showVolumeFeedback = true
                            showBrightnessFeedback = false
                            volumeLevel = (volumeLevel - dragAmount / 500).coerceIn(0f, maxVolume)
                            audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                volumeLevel.toInt(),
                                0
                            )
                        } else {
                            // Right Side Gesture: Brightness Control
                            showBrightnessFeedback = true
                            showVolumeFeedback = false
                            brightness = (brightness - dragAmount / 500).coerceIn(0f, 1f)
                            val layoutParams = activity.window.attributes
                            layoutParams.screenBrightness = brightness
                            activity.window.attributes = layoutParams
                        }
                    },
                    onDragEnd = {
                        // Hide feedback UI after gesture ends
                        showVolumeFeedback = false
                        showBrightnessFeedback = false
                    }
                )
            }
    ) {
        if (isMinimized) {
            // Mini-player implementation...
            // ...
        } else {
            // Main Player View
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = viewModel.player
                        useController = false
                        resizeMode = playerState.resizeMode
                        keepScreenOn = playerState.isPlaying
                    }
                },
                modifier = Modifier.fillMaxSize()
                    .clickable { showControls = !showControls }
            )

            // Gesture Feedback Icons
            if (showVolumeFeedback) {
                GestureFeedbackUI(
                    icon = painterResource(R.drawable.volume_up_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    value = (volumeLevel / maxVolume * 100).toInt()
                )
            }
            if (showBrightnessFeedback) {
                GestureFeedbackUI(
                    icon = painterResource(R.drawable.brightness_7_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    value = (brightness * 100).toInt()
                )
            }

            // Middle Controls (Play/Pause, Seek, etc.)
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MiddleControls(
                    isPlaying = playerState.isPlaying,
                    onPlayPauseClick = { viewModel.onPlayPauseClick() },
                    onSeekForwardClick = { viewModel.player.seekForward() },
                    onSeekBackwardClick = { viewModel.player.seekBack() },
                    modifier = Modifier.fillMaxSize(),
                    onClick = { showControls = !showControls },
                )
            }

            // Video Controls (Upper and Bottom Controls)
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    UpperControls(
                        videoTitle = playerState.videoTitle.toString(),
                        onBackClick = onBackClick,
                        onMinimizeClick = { isMinimized = true },
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    BottomControls(
                        player = viewModel.player,
                        totalTime = playerState.currentVideoItem?.duration ?: 0L,
                        onRotateScreenClick = onRotateScreenClick,
                        resizeMode = playerState.resizeMode,
                        onResizeModeChange = { viewModel.onResizeClick() },
                        isRotationLocked = viewModel.isRotationLocked.observeAsState(false).value,
                        onLockClick = { viewModel.toggleRotationLock() },
                        brightness = brightness,
                        onBrightnessChange = { newBrightness ->
                            brightness = newBrightness
                            val layoutParams = activity.window.attributes
                            layoutParams.screenBrightness = brightness
                            activity.window.attributes = layoutParams
                        },
                        volumeLevel = volumeLevel,
                        onSubtitlesClick = {},
                        onVolumeChange = { newVolumeLevel ->
                            volumeLevel = newVolumeLevel
                            audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                (volumeLevel / maxVolume).toInt(),
                                0
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GestureFeedbackUI(icon: Painter, value: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "$value%",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpperControls(
    videoTitle: String,
    onBackClick: () -> Unit,
    onMinimizeClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.background(Color.Transparent)
    ){
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(Color.Black.copy(0.7f)))
        TopAppBar(
            title = {
                Text(
                    text = videoTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        stringResource(id = R.string.go_back),
                        tint = Color.White
                    )
                }
            },

            colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
        )
        Spacer(modifier = Modifier.size(6.dp))
        IconButton(onClick = onMinimizeClick) {
            Icon(
                painter = painterResource(R.drawable.close_fullscreen_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                contentDescription = "Minimize Video",
                tint = Color.White
            )
        }
    }

}

@Composable
private fun MiddleControls(
    isPlaying: Boolean,
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekForwardClick: () -> Unit,
    onSeekBackwardClick: () -> Unit,
    modifier: Modifier = Modifier, // Add isRotationLocked parameter
){

        Row(
            modifier = modifier
        ) {

            MiddleControlsItem(
                icon = R.drawable.keyboard_double_arrow_left_24dp_e8eaed_fill0_wght400_grad0_opsz24,
                contentDescription = R.string.seek_backward,
                onIconClick = onSeekBackwardClick,
                onSingleClick = onClick,
                onDoubleClick = onSeekBackwardClick,
                modifier = modifier
                    .weight(1f)
            )

            MiddleControlsItem(
                icon = if (isPlaying) R.drawable.pause_24dp_e8eaed_fill0_wght400_grad0_opsz24 else R.drawable.play_arrow_24dp_e8eaed_fill0_wght400_grad0_opsz24,
                contentDescription = R.string.play_pause,
                onIconClick = onPlayPauseClick,
                onSingleClick = onClick,
                onDoubleClick = onPlayPauseClick,
                modifier = modifier
                    .weight(1f)
            )

            MiddleControlsItem(
                icon = R.drawable.keyboard_double_arrow_right_24dp_e8eaed_fill0_wght400_grad0_opsz24,
                contentDescription = R.string.seek_forward,
                onIconClick = onSeekForwardClick,
                onSingleClick = onClick,
                onDoubleClick = onSeekForwardClick,
                modifier = modifier
                    .weight(1f)
            )
                // Volume Slider
            }
        }


@UnstableApi
@Composable
private fun BottomControls(
    player: Player,
    totalTime: Long,
    onRotateScreenClick: () -> Unit,
    resizeMode: Int,
    onResizeModeChange: () -> Unit,
    onLockClick: () -> Unit,
    isRotationLocked: Boolean,
    onSubtitlesClick: () -> Unit,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    volumeLevel: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
){
    var currentTime by remember{
        mutableLongStateOf(player.currentPosition)
    }

    var isSeekInProgress by remember{
        mutableStateOf(false)
    }

    var showSettingsMenu by remember { mutableStateOf(false) }

    val timerCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit){
        timerCoroutineScope.launch {
            while(true){
                delay(500)
                if(!isSeekInProgress){
                    currentTime = player.currentPosition
                    Log.d("PlayerScreen", "timer running $currentTime")
                }
            }
        }
    }

    Column(
        modifier = modifier.background(Color.Transparent)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ){
            Text(
                text = "${currentTime.toHhMmSs()}-${totalTime.toHhMmSs()}",
                modifier = Modifier.padding(horizontal = 12.dp),
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onResizeModeChange) {
                Icon(
                    painterResource(id = if(resizeMode  == AspectRatioFrameLayout.RESIZE_MODE_FIT) R.drawable.aspect_ratio_24dp_e8eaed_fill0_wght400_grad0_opsz24 else R.drawable.fit_screen_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    stringResource(id = R.string.toggle_fitScreen),
                    tint = Color.White
                )
            }
            if(isRotationLocked) {
                IconButton(
                    onClick = onRotateScreenClick,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        painterResource(id = R.drawable.screen_rotation_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        stringResource(id = R.string.rotate_screen),
                        tint = Color.White
                    )
                }
            }
            IconButton(
                onClick = onLockClick,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
            ) {
                Icon(
                    painter = painterResource(if (isRotationLocked) R.drawable.lock_24dp_e8eaed_fill0_wght400_grad0_opsz24 else R.drawable.lock_open_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    contentDescription = if (isRotationLocked) "Unlock Rotation" else "Lock Rotation",
                    tint = Color.White
                )
            }

            IconButton(onClick = { showSettingsMenu = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
            // Dropdown Menu with Brightness and Volume Sliders
            SettingsDropdownMenu(
                expanded = showSettingsMenu,
                onDismissRequest = { showSettingsMenu = false },
                brightness = brightness,
                onBrightnessChange = onBrightnessChange,
                volumeLevel = volumeLevel,
                onVolumeChange = onVolumeChange,
                modifier = Modifier
            )
        }

        // Brightness and Volume Sliders


        CustomSeekBar(
            player = player,
            isSeekInProgress = { isInProgress ->
                isSeekInProgress = isInProgress
            },
            onSeekBarMove = { position ->
                currentTime = position
            },
            totalDuration = totalTime,
            currentTime = currentTime,
            modifier = Modifier.padding(12.dp)
        )
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MiddleControlsItem(
    @DrawableRes icon: Int,
    @StringRes contentDescription: Int,
    onIconClick: () -> Unit,
    onSingleClick: () -> Unit,
    onDoubleClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .combinedClickable(
                onClick = onSingleClick,
                onDoubleClick = onDoubleClick
            ),
        contentAlignment = Alignment.Center
    ){
        FilledIconButton(
            onClick = onIconClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.White
            )
        ) {
            Icon(
                painterResource(id = icon),
                stringResource(id = contentDescription)
            )
        }

    }
}

@UnstableApi
@Composable
fun CustomSeekBar(
    player: Player,
    isSeekInProgress: (Boolean) -> Unit,
    onSeekBarMove: (Long) -> Unit,
    currentTime: Long,
    totalDuration: Long,
    modifier: Modifier = Modifier
){
    val primaryColor = Color.White

    AndroidView(
        factory = { context ->

            val listener = object: TimeBar.OnScrubListener{

                var previousScrubPosition = 0L

                override fun onScrubStart(timeBar: TimeBar, position: Long) {
                    isSeekInProgress(true)
                    previousScrubPosition = position
                }

                override fun onScrubMove(timeBar: TimeBar, position: Long) {
                    onSeekBarMove(position)
                }

                override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                    if(canceled){
                        player.seekTo(previousScrubPosition)
                    }else{
                        player.seekTo(position)
                    }
                    isSeekInProgress(false)
                }

            }

            DefaultTimeBar(context).apply {
                setScrubberColor(primaryColor.toArgb())
                setPlayedColor(primaryColor.toArgb())
                setUnplayedColor(primaryColor.copy(0.3f).toArgb())
                addListener(listener)
                setDuration(totalDuration)
                setPosition(player.currentPosition)
            }
        },
        update = {
            it.apply {
                setPosition(currentTime)
            }
        },

        modifier = modifier
    )
}

@Composable
fun SettingsDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    volumeLevel: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(Color(0xFF222831)) // Semi-transparent background
    ) {
        Row {
            BrightnessSlider(brightness = brightness, onBrightnessChange = onBrightnessChange)
            Spacer(modifier = Modifier.size(16.dp))
            VolumeSlider(volumeLevel = volumeLevel, onVolumeChange = onVolumeChange)
        }
    }

    }



@Composable
fun VolumeSlider(
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    volumeLevel: Float
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .width(100.dp)
            .background(Color.Transparent)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "${(volumeLevel * 100).toInt()}%",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = volumeLevel,
            onValueChange = {
                onVolumeChange(it)
            },
            valueRange = 0f..1f,
            modifier = Modifier
                .height(200.dp)
                .rotate(-90f) // Rotate to make the slider vertical
                .padding(vertical = 8.dp),
            colors = SliderColors(
                thumbColor = Color(0xFF892CDC),
                activeTrackColor = Color(0xFF892CDC),
                inactiveTrackColor = Color(0xFF892CDC).copy(alpha = 0.3f),
                activeTickColor = Color(0xFF892CDC),
                inactiveTickColor = Color(0xFF892CDC),
                disabledThumbColor = Color(0xFF892CDC),
                disabledActiveTrackColor = Color(0xFF892CDC),
                disabledActiveTickColor = Color(0xFF892CDC),
                disabledInactiveTrackColor = Color(0xFF892CDC),
                disabledInactiveTickColor = Color(0xFF892CDC)
            )
        )

        Text(
            text = "Volume",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Icon(
            painter = painterResource(id = android.R.drawable.ic_lock_silent_mode_off),
            contentDescription = "Volume Icon",
            tint = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun BrightnessSlider(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight()
            .width(100.dp)
            .background(Color.Transparent)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "${(brightness * 100).toInt()}%",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = brightness,
            onValueChange = {
                onBrightnessChange(it)
            },
            valueRange = 0f..1f,
            modifier = Modifier
                .height(200.dp)
                .rotate(-90f) // Rotate to make the slider vertical
                .padding(vertical = 8.dp),
            colors = SliderColors(
                thumbColor = Color(0xFF892CDC),
                activeTrackColor = Color(0xFF892CDC),
                inactiveTrackColor = Color(0xFF892CDC).copy(alpha = 0.3f),
                activeTickColor = Color(0xFF892CDC),
                inactiveTickColor = Color(0xFF892CDC),
                disabledThumbColor = Color(0xFF892CDC),
                disabledActiveTrackColor = Color(0xFF892CDC),
                disabledActiveTickColor = Color(0xFF892CDC),
                disabledInactiveTrackColor = Color(0xFF892CDC),
                disabledInactiveTickColor = Color(0xFF892CDC)
            )
        )

        Text(
            text = "Brightness",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Choose an appropriate icon for brightness
            contentDescription = "Brightness Icon",
            tint = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MiniPlayer(
    player: Player,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(200.dp, 112.dp) // Mini player dimensions
            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
            .padding(8.dp) // Bottom-right corner of the screen
            .offset(x = (-16).dp, y = (-16).dp)
            .fillMaxSize() ,// Margin from bottom-right
        contentAlignment = Alignment.BottomEnd
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Mini-Player",
                tint = Color.White
            )
        }
    }
}





