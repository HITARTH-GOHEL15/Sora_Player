package com.example.soraplayer.Presentation.mainScreenComponents

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.MusicPlayer.MusicPlayerBottomBar
import com.example.soraplayer.MusicPlayer.MusicPlayerViewModel
import com.example.soraplayer.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.InternalLandscapistApi
import com.skydoves.landscapist.glide.GlideImage

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MusicList(
    musicTracks: List<MusicItem>,
    modifier: Modifier = Modifier
) {
    val viewModel : MusicPlayerViewModel = viewModel(factory = MusicPlayerViewModel.factory)
    // Scaffold layout with a bottom bar
    Scaffold(
        modifier = modifier
            .background(color = Color(0xFF222831))
            .fillMaxSize(),
        bottomBar = {
            // Ensure the music player bottom bar is above the navigation bar
            MusicPlayerBottomBar(
                viewModel = viewModel,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Use innerPadding to prevent overlap with bottom bar
                .background(Color(0xFF222831)) // Background color for the grid
        ) {
            // Adjust the bottom padding to ensure the grid does not overlap with the bottom bar
            Row(
                modifier = Modifier
                    .fillMaxSize() // Adjust this padding as per the height of your bottom navigation bar
            ) {
                // Music Grid
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(musicTracks) { musicItem ->
                        MusicItemRow(musicItem = musicItem, onItemClick = { item ->
                            viewModel.updateCurrentTrack(item) // Update current track in ViewModel
                        })
                    }
                }
            }
        }
    }
}

@OptIn(InternalLandscapistApi::class)
@Composable
fun MusicItemRow(
    musicItem: MusicItem,
    onItemClick: (MusicItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(musicItem) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
          if(musicItem.artWork != null) {
              AsyncImage(
                  model = musicItem.artWork,
                  contentDescription = null,
                  modifier = Modifier
                      .size(50.dp)
                      .clip(RoundedCornerShape(8.dp))
              )
          } else {
              Icon(
                  modifier = Modifier.size(50.dp),
                  painter = painterResource(R.drawable.music_note_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                  contentDescription = null
              )
          }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = musicItem.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "${musicItem.artist} • ${musicItem.album}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.8.dp)
            .background(Color.DarkGray) // Color for the border line
    )
}