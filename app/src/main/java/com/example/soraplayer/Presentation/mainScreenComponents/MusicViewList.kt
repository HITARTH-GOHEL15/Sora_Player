package com.example.soraplayer.Presentation.mainScreenComponents

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.rememberDrawablePainter

@Composable
fun MusicList(
    musicTracks: List<MusicItem>,
    onItemClick: (MusicItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 105.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(musicTracks) { musicItem ->
            MusicItemRow(musicItem = musicItem, onItemClick = onItemClick)
        }
    }
}

@Composable
fun MusicItemRow(
    musicItem: MusicItem,
    onItemClick: (MusicItem) -> Unit
) {
    val albumArtUri = musicItem.albumArtUri ?: painterResource(id = R.drawable.music_note_24dp_e8eaed_fill0_wght400_grad0_opsz24)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(musicItem) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = albumArtUri
            ),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(64.dp)
                .padding(end = 16.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = musicItem.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${musicItem.artist} • ${musicItem.album}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}