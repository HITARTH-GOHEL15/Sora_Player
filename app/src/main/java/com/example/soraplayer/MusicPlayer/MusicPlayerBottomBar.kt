package com.example.soraplayer.MusicPlayer

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.example.soraplayer.R

@OptIn(UnstableApi::class)
@Composable
fun MusicPlayerBottomBar(
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier,
) {
    val musicPlayerState by viewModel.musicPlayerState.collectAsState()
    val currentTrack = musicPlayerState.currentTrack
    val context = LocalContext.current
    val playerActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
                result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getLongExtra("CURRENT_POSITION", 0L)?.let { position ->
                    viewModel.updateCurrentPosition(position)
                }
            }
        }
    )

    if (currentTrack != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 115.dp) // Add padding to avoid overlap with bottom navigation
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable {
                    // Pass necessary information to MusicPlayerActivity when clicked
                    val playerIntent =
                        Intent(
                            context,
                            MusicPlayerActivity::class.java
                        ).apply {
                            data = currentTrack.uri
                            putExtra("CURRENT_POSITION", musicPlayerState.currentPosition)

                        }
                    playerActivityLauncher.launch(playerIntent)
                }
        ) {
            // Album Art Thumbnail
            AsyncImage(
                model = currentTrack.artWork,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(56.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Track and Artist Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currentTrack.name,
                    color = Color.White,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = currentTrack.artist,
                    color = Color.Gray,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
                // In MusicPlayerBottomBar
                Text(
                    text = viewModel.formatPosition(musicPlayerState.currentPosition),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )

            }

            // Controls (Backward, Play/Pause, Forward)
            IconButton(
                onClick = { viewModel.onSeekBackwardClick() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.skip_previous_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    contentDescription = "Rewind",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { viewModel.onPlayPauseClick() },
                modifier = Modifier.size(40.dp)
            ) {
                val icon = if (musicPlayerState.isPlaying) {
                    painterResource(id = R.drawable.pause_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24)
                } else {
                    painterResource(id = R.drawable.play_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24)
                }
                Icon(painter = icon, contentDescription = "Play/Pause", tint = Color.White)
            }

            IconButton(
                onClick = { viewModel.onSeekForwardClick() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.skip_next_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    contentDescription = "Fast Forward",
                    tint = Color.White
                )
            }
        }
        }
    }

