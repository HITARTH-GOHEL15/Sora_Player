package com.example.soraplayer.Player

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.ImageView
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.example.soraplayer.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@SuppressLint("SuspiciousIndentation")
@OptIn(UnstableApi::class)
@Composable
fun PlayerBottomBar(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier,
) {
    val playerState by viewModel.playerState.collectAsState()
    val currentTrack = playerState.currentVideoItem ?: playerState.lastPlayedVideoItem
    val context = LocalContext.current
    val playerActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 106.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1E1E), Color(0xFF121212)),
                    startY = 0f,
                    endY = 100f
                )
            )
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                val playerIntent = Intent(context, PlayerActivity::class.java).apply {
                    currentTrack?.let { data = it.uri }
                    putExtra("CURRENT_POSITION", playerState.currentPosition)
                }
                playerActivityLauncher.launch(playerIntent)
            }
    ) {
        // Album Art Thumbnail
        currentTrack?.let { track ->
            AndroidView(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.6f)),
                factory = { context -> ImageView(context).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                update = { imageView ->
                    Glide.with(context)
                        .load(track.uri)
                        .placeholder(R.drawable.video_library_24dp_e8eaed_fill0_wght400_grad0_opsz24)
                        .into(imageView)
                }
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Track and Artist Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            currentTrack?.let {
                Text(
                    text = it.name,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = it.name, // replace with actual artist name
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light)
                )
            }
        }

        IconButton(
            onClick = { viewModel.onPlayPauseClick() },
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(
                    if (playerState.isPlaying) {
                        R.drawable.pause_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24
                    } else {
                        R.drawable.play_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24
                    }
                ),
                contentDescription = "Play/Pause",
                tint = Color.White,
                modifier = Modifier.size(32.dp) // Adjust icon size
            )
        }
    }
}



