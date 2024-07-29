package com.example.soraplayer.Presentation.mainScreenComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
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
import com.example.soraplayer.Model.FolderItem
import com.example.soraplayer.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun FolderItemListLayout(
    foldersList: List<FolderItem>,
    onFolderItemClick: (FolderItem) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(8.dp))
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = modifier
                    .padding(bottom = 4.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                items(foldersList, key = { it.name }) { folderItem ->
                    FolderListItem(folderItem = folderItem, onItemClick = onFolderItemClick)
                }

            }
        }
    }
}

@Composable
private fun FolderListItem(
    folderItem: FolderItem,
    onItemClick: (FolderItem) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .clickable(
                onClick = {
                    onItemClick(folderItem)
                }
            )
            .padding(start  = 12.dp , end = 12.dp, top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.file_folder),
            tint = Color(0xFF049FFD),
            contentDescription = folderItem.name,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = folderItem.name,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 4.dp)
        )
    }
}