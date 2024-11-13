package com.example.soraplayer.Presentation.mainScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soraplayer.Model.FolderItem
import com.example.soraplayer.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun FolderItemGridLayout(
    foldersList: List<FolderItem>,
    onFolderItemClick: (FolderItem) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyVerticalGrid(
                modifier = modifier
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Top,
                columns = GridCells.Fixed(3),
                ) {

                items(foldersList, key = { it.name }) { folderItem ->
                    FolderGridItem(folderItem = folderItem, onItemClick = onFolderItemClick)
                }

            }
        }
    }
}

@Composable
private fun FolderGridItem(
    folderItem: FolderItem,
    onItemClick: (FolderItem) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .clickable(
                onClick = {
                    onItemClick(folderItem)
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.file_folder),
            tint = Color(0xFF892CDC),
            contentDescription = folderItem.name,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = folderItem.name,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        }
    }