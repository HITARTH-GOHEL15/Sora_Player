package com.example.soraplayer.MusicPlayer

import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import coil.compose.rememberAsyncImagePainter
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.R
import com.example.soraplayer.Utils.toHhMmSs
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun MusicPlayerScreen(
    viewModel: MusicPlayerViewModel,
    onBackClick: () -> Unit,
) {
    val musicPlayerState by viewModel.musicPlayerState.collectAsState()
    var showControls by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(showControls) {
        if (showControls) {
            delay(10000)
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section for navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                ShareDropDownMenu(
                    onShare = {
                        showShareDialog = true
                    }
                )

                if (showShareDialog) {
                    AlertDialog(
                        onDismissRequest = { showShareDialog = false },
                        title = {
                            Text(
                                "Share Music",
                                color = Color(0xFFD9ACF5)
                            )
                        },
                        text = {
                            Text(
                                "Share this Music with others?",
                                color = Color.White
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    shareMusic(context, musicPlayerState.currentTrack!!)
                                    showShareDialog = false
                                }
                            ) {
                                Text(
                                    "Share",
                                    color = Color(0xFFD9ACF5)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showShareDialog = false }
                            ) {
                                Text(
                                    "Cancel",
                                    color = Color.White
                                )
                            }
                        },
                        containerColor = Color.Transparent,
                        modifier = Modifier.background(color = Color(0xFF222831))
                    )
                }

            }

            // Album Art
            if (
                musicPlayerState.currentTrack?.artWork != null
            ) {
                Image(
                    painter = rememberAsyncImagePainter(musicPlayerState.currentTrack?.artWork),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(350.dp)
                        .clip(shape = MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.music_note_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    contentDescription = "Music Note",
                    modifier = Modifier
                        .size(350.dp)
                        .clip(shape = MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Track Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = musicPlayerState.currentTrack?.name ?: "Unknown Title",
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = musicPlayerState.currentTrack?.artist ?: "Unknown Artist",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = musicPlayerState.currentTrack?.album ?: "Unknown Album",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Playback Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                MusicSeekBar(
                    player = viewModel.player,
                    totalTime = musicPlayerState.currentTrack?.duration ?: 0L,
                    modifier = Modifier.fillMaxWidth()
                )
            }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                ) {
                    IconButton(onClick = { viewModel.onSeekBackwardClick() }) {
                        Icon(
                            painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_skip_back_10),
                            contentDescription = "Seek Backward",
                            tint = Color.White,
                            modifier = Modifier
                                .size(150.dp)

                        )
                    }
                    Spacer(modifier = Modifier.width(36.dp))
                    IconButton(onClick = { viewModel.onPlayPauseClick() }) {
                        Icon(
                            painter = painterResource(id = if (musicPlayerState.isPlaying) R.drawable.pause_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24 else R.drawable.play_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier
                                .size(250.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(36.dp))
                    IconButton(
                        onClick = { viewModel.onSeekForwardClick() }
                    ) {
                        Icon(
                            painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_skip_forward_10),
                            contentDescription = "Seek Forward",
                            tint = Color.White,
                            modifier = Modifier
                                .size(150.dp)

                        )
                    }
                }
            }
    }
}

@Composable
fun MusicSeekBar(
    player: Player,
    totalTime: Long,
    modifier: Modifier = Modifier
) {
    var position by remember { mutableLongStateOf(player.currentPosition) }
    LaunchedEffect(player) {
        while (true) {
            position = player.currentPosition
            delay(1000)
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TrackSlider(
            value = position.toFloat(),
            onValueChange = {
                position = it.toLong()
            },
            onValueChangeFinished = {
                player.seekTo(position)
            },
            songDuration = totalTime.toFloat()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = position.toHhMmSs(), color = Color.White)
            Text(text = totalTime.toHhMmSs(), color = Color.White)
        }
    }
}

@Composable
fun TrackSlider(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float
) {
    Slider(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        onValueChangeFinished = {

            onValueChangeFinished()

        },
        valueRange = 0f..songDuration,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.Gray,
            inactiveTrackColor = Color.DarkGray,
        )
    )
}

@Composable
fun ShareDropDownMenu(
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    IconButton(
        onClick = {
            expanded = true
        },
    ) {
        Icon(
            Icons.Default.Share,
            contentDescription = null,
            tint = Color.White,
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .focusRequester(focusRequester)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    "Share",
                    color = Color(0xFFD9ACF5)
                )
            },
            onClick = {
                expanded = false
                onShare()
            }
        )
    }

}

private fun shareMusic(context: Context, musicItem: MusicItem) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, musicItem.uri)
        type = "audio/*" // Adjust MIME type based on the file type
    }
    context.startActivity(Intent.createChooser(intent, "Share Music via"))
}


