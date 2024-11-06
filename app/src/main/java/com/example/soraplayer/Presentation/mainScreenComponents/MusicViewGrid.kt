package com.example.soraplayer.Presentation.mainScreenComponents

import androidx.compose.runtime.Composable

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.R

@Composable
fun MusicGrid(
    musicTracks: List<MusicItem>,
    onItemClick: (MusicItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 105.dp),
        columns = GridCells.Fixed(2)
    ) {
        items(musicTracks) { musicItem ->
            MusicItemColumn(musicItem = musicItem, onItemClick = onItemClick)
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
