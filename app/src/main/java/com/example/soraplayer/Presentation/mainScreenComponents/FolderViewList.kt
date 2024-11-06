package com.example.soraplayer.Presentation.mainScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soraplayer.MainScreen.MainViewModel
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
                    FolderListItem(
                        folderItem = folderItem,
                        onItemClick = onFolderItemClick,
                        modifier = Modifier
                    )
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
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
    var expanded by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
            tint  = Color(0xFF892CDC),
            contentDescription = folderItem.name,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = folderItem.name,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier
                .padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                // Handle more menu button click
                expanded = true
            },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = folderItem.name,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Rename") },
                onClick = {
                    newName = folderItem.name // Set initial name for rename dialog
                    showRenameDialog = true
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    showDeleteConfirmation = true
                    expanded = false
                }
            )
        }
    }

    // Rename Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Folder") },
            text = {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New Folder Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRenameDialog = false
                    }
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                Button(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this folder? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    // Add the bottom border line here
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.8.dp)
            .background(Color.DarkGray) // Color for the border line
    )
}