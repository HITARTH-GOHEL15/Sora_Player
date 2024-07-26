package com.example.soraplayer.Presentation.mainScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.Utils.toHhMmSs
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun VideoItemGridLayout(
    videoList: List<VideoItem>,
    onVideoItemClick: (VideoItem) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.Center,
        columns = StaggeredGridCells.Fixed(2)
    ){
        items(videoList, key = {it.name}){videoItem ->
            VideoGridItem(
                videoItem = videoItem,
                onItemClick = onVideoItemClick,
                modifier = Modifier.padding(6.dp)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun VideoGridItem(
    videoItem: VideoItem,
    onItemClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .padding(2.dp)
            .height(200.dp)
            .clickable {
                onItemClick(videoItem)
            }
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clip(shape = MaterialTheme.shapes.small),
            ) {
                GlideImage(
                    imageModel = { videoItem.uri },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    ),
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .matchParentSize()
                                .align(Alignment.Center)
                        )
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .background(color = Color.Transparent)
                ) {
                    FlowRow(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        FlowRowItem(text = videoItem.duration.toHhMmSs())
                    }
                }
            }
                Text(
                    text = videoItem.name,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 5.dp ,top = 5.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

               Row(
                   modifier = Modifier
                       .fillMaxWidth()
               ) {
                   FlowRow(
                       modifier = Modifier
                   ) {
                       FlowRowItem(text = "${videoItem.size / 1000000} MB")
                       FlowRowItem(text = "${videoItem.height} x ${videoItem.width}")
                   }
                   Icon(
                       imageVector = Icons.Filled.MoreVert, 
                       contentDescription = null
                   )
                   
               }
            }


    }
}

@Composable
private fun FlowRowItem(
    text: String,
    modifier: Modifier = Modifier
){
    ElevatedCard(
        modifier = modifier
            .padding(4.dp),
        shape = CutCornerShape(2.dp),
    ){
        Text(
            text,
            fontSize = 10.sp,
            modifier = Modifier.padding(1.dp)
        )
    }
}

