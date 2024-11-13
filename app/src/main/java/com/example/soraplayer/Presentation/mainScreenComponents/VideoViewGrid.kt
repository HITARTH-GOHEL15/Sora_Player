package com.example.soraplayer.Presentation.mainScreenComponents

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.service.credentials.Action
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.MainScreen.LayoutChange
import com.example.soraplayer.MainScreen.MainViewModel
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.Player.PlayerBottomBar
import com.example.soraplayer.Player.PlayerViewModel
import com.example.soraplayer.Utils.toHhMmSs
import com.example.soraplayer.ui.theme.poppins
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@androidx.annotation.OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VideoItemGridLayout(
    videoList: List<VideoItem>,
    onVideoItemClick: (VideoItem) -> Unit,
    modifier: Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    scrollState: LazyStaggeredGridState
) {

    val viewModel : PlayerViewModel = viewModel(factory = PlayerViewModel.factory)
    // Scaffold layout with a bottom bar
    Scaffold(
        modifier = modifier
            .background(color = Color(0xFF222831))
            .fillMaxSize(),
        bottomBar = {
            // Ensure the music player bottom bar is above the navigation bar
            PlayerBottomBar(
                viewModel = viewModel
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = onRefresh,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = onRefresh,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        LazyVerticalStaggeredGrid(
                            modifier = modifier,
                            state = scrollState,
                            horizontalArrangement = Arrangement.Center,
                            columns = StaggeredGridCells.Fixed(2)
                        ) {
                            items(videoList, key = { it.name }) { videoItem ->
                                VideoGridItem(
                                    videoItem = videoItem,
                                    onItemClick = onVideoItemClick,
                                    modifier = Modifier.padding(6.dp),
                                    videoUri = videoItem.uri
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun VideoGridItem(
    videoItem: VideoItem,
    onItemClick: (VideoItem) -> Unit,
    videoUri: Uri = videoItem.uri,
    modifier: Modifier = Modifier
){
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
    var showRenameDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(videoItem.name) }
    var showShareDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                        FlowRowItem2(text = videoItem.duration.toHhMmSs())
                    }
                }
            }
                    Text(
                        text = videoItem.name,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(start = 5.dp, top = 5.dp),
                        color = Color.White
                    )
            Spacer(modifier = Modifier.padding(1.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    FlowRow(
                        modifier = Modifier
                            .background(color = Color.Transparent)
                    ) {
                        FlowRowItem(
                            text = "${videoItem.size / 1000000} MB",
                            modifier = Modifier.background(color = Color.Transparent)
                        )
                        FlowRowItem(
                            text = videoItem.date,
                            modifier = Modifier.background(color = Color.Transparent)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        MoreVertMenu(
                            onRename = {
                                newName = videoItem.name
                                showRenameDialog = true
                            },
                            onRemove = {
                                showRemoveDialog = true

                            },
                            onShare = {
                                showShareDialog = true
                            },
                            modifier = Modifier
                                .background(color = Color(0xFF222831))

                        )
                    }
                }
            }
            }

        // Remove Dialog
        if (showRemoveDialog) {
            AlertDialog(
                onDismissRequest = { showRemoveDialog = false },
                title = { Text(
                    "Remove Video",
                    color = Color(0xFFD9ACF5)
                ) },
                text = { Text(
                    "Are you sure you want to remove this video?",
                    color = Color.White
                ) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Handle removing logic here
                            mainViewModel.deleteVideo(context, videoUri)
                            showRemoveDialog = false
                        }
                    ) {
                        Text(
                            "Remove",
                            color = Color(0xFFD9ACF5)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showRemoveDialog = false }
                    ) {
                        Text(
                            "Cancel",
                            color = Color.White
                        )
                    }
                },
                containerColor = Color.Transparent,
                modifier = Modifier.background(color = Color(0xFF222831))
            )
        }

        if (showShareDialog) {
            AlertDialog(
                onDismissRequest = { showShareDialog = false },
                title = {
                    Text(
                        "Share Video",
                        color = Color(0xFFD9ACF5)
                    )
                        },
                text = { Text(
                    "Share this video with others?",
                    color = Color.White
                ) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            shareVideo(context, videoItem)
                            showShareDialog = false
                        },
                    ) {
                        Text(
                            "Share",
                            color = Color(0xFFD9ACF5)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showShareDialog = false }
                    ) {
                        Text(
                            "Cancel",
                            color = Color.White
                        )
                    }
                },
                containerColor = Color.Transparent,
                modifier = Modifier.background(color = Color(0xFF222831))
            )
        }
    }


@Composable
private fun FlowRowItem(
    text: String,
    modifier: Modifier = Modifier
){
    ElevatedCard(
        modifier = modifier
            .padding(4.dp)
            .background(color = Color.Transparent),
        shape = CutCornerShape(2.dp),
         CardDefaults.cardColors(
            Color.Transparent
        )
    ){
        Text(
            text,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.padding(1.dp)
        )

    }
}

@Composable
private fun FlowRowItem2(
    text: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .padding(4.dp)
            .background(color = Color.Transparent),
        shape = CutCornerShape(2.dp),
        CardDefaults.cardColors(
            Color.Black
        )
    ){
        Text(
            text,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.padding(1.dp)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreVertMenu(
    onRename: () -> Unit,
    onRemove: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }


    IconButton(
        onClick = { expanded = true },
        modifier = Modifier
            .size(18.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
            tint = Color.White,
        )
    }

    val focusRequester = remember {
        FocusRequester()
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .focusRequester(focusRequester)
            .background(Color(0xFF222831))
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    "Remove",
                    color = Color(0xFFD9ACF5)
                )
            },
            onClick = {
                expanded = false
                onRemove()
            },
        )
        DropdownMenuItem(
            text = {
                Text(
                    "Share",
                    color = Color(0xFFD9ACF5)
                )
            },
            onClick = {
                expanded = false
                onShare()
            },

        )
    }

}

private fun shareVideo(context: Context, videoItem: VideoItem) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, videoItem.uri)
        type = "video/*" // Adjust MIME type based on the file type
    }
    context.startActivity(Intent.createChooser(intent, "Share video via"))
}

