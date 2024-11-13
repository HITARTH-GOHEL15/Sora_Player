package com.example.soraplayer.Presentation.mainScreenComponents

import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.MusicPlayer.MusicPlayerActivity
import com.example.soraplayer.MusicPlayer.MusicPlayerBottomBar
import com.example.soraplayer.MusicPlayer.MusicPlayerScreen
import com.example.soraplayer.MusicPlayer.MusicPlayerViewModel
import com.example.soraplayer.R

@OptIn(UnstableApi::class)
@Composable
fun MusicGrid(
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
            Column(
                modifier = Modifier
                    .fillMaxSize() // Adjust this padding as per the height of your bottom navigation bar
            ) {
                // Music Grid
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(), // Ensure it doesn't overlap with the navigation
                    columns = GridCells.Fixed(2)
                ) {
                    items(musicTracks) { musicItem ->
                        MusicItemColumn(musicItem = musicItem, onItemClick = { item ->
                            viewModel.updateCurrentTrack(item) // Update current track in ViewModel
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun MusicItemColumn(
    musicItem: MusicItem,
    onItemClick: (MusicItem) -> Unit
) {

    Box(
        modifier = Modifier
            .background(shape = MaterialTheme.shapes.medium , color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .clickable { onItemClick(musicItem) }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(musicItem.artWork != null) {
                AsyncImage(
                    model = musicItem.artWork,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .fillMaxWidth()
                )
            } else {
                Icon(
                    modifier = Modifier.size(200.dp),
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
    }
}
