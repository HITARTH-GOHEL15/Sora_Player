package com.example.soraplayer.MusicPlayer

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import coil.compose.rememberAsyncImagePainter
import com.example.soraplayer.R
import com.example.soraplayer.Utils.toHhMmSs
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun MusicPlayerScreen(
    viewModel: MusicPlayerViewModel,
    onBackClick: () -> Unit
) {
    val musicPlayerState by viewModel.musicPlayerState.collectAsState()
    var showControls by remember { mutableStateOf(false) }

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
                Text(
                    text = musicPlayerState.currentTrack?.name ?: "No Track Playing",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Album Art
            Image(
                painter = rememberAsyncImagePainter(musicPlayerState.currentTrack?.albumArtUri),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            // Track Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { viewModel.onSeekBackwardClick() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_double_arrow_left_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = "Seek Backward",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { viewModel.onPlayPauseClick() }) {
                        Icon(
                            painter = painterResource(id = if (musicPlayerState.isPlaying) R.drawable.pause_24dp_e8eaed_fill0_wght400_grad0_opsz24 else R.drawable.play_arrow_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = "Play/Pause",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { viewModel.onSeekForwardClick() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_double_arrow_right_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = "Seek Forward",
                            tint = Color.White
                        )
                    }
                }
            }

            // Bottom Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Add to favorite */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.favorite_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { /* Delete Track */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { /* Share Track */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.share_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Share",
                        tint = Color.White
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