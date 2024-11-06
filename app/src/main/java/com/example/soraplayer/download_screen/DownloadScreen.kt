package com.example.soraplayer.download_screen

import DownloadItem
import DownloadViewModel
import DownloadViewModelFactory
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.Player.PlayerActivity
import com.example.soraplayer.R
import com.example.soraplayer.ui.theme.poppins

@OptIn(UnstableApi::class)
@Composable
fun DownloadsScreen(context: Context , modifier: Modifier) {
    var url by remember { mutableStateOf("") }

    val viewModel: DownloadViewModel = viewModel(
        factory = DownloadViewModelFactory(context)
    )

    val downloads = viewModel.downloads.collectAsState().value // Collect the state of the downloads


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF222831))
            .padding(16.dp)
    ) {
        // Input field for URL
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text(
                "Enter Download URL",
                color = Color.White
            ) },
            modifier = Modifier
                .fillMaxWidth()
            ,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF892CDC),
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF892CDC),
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to add new download
        Button(
            onClick = {
                if (url.isNotEmpty()) {
                    viewModel.addDownload(url)
                    url = ""  // Clear the field
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF892CDC),
                contentColor = Color.White
            )
        ) {
            Text("Add Download")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // List of current downloads
        Text(
            text = "Current Downloads",
            style = MaterialTheme.typography.titleLarge,
            fontFamily = poppins
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(downloads) { download ->
                DownloadItemRow(
                    download = download,
                    onPauseClick = {
                        if (download.status == DownloadManager.STATUS_RUNNING) {
                            viewModel.pauseDownload(download.id)
                        } else if (download.status == DownloadManager.STATUS_PAUSED || download.status == DownloadManager.STATUS_FAILED) {
                            viewModel.resumeDownload(download.id)
                        }
                    },
                    onDeleteClick = { viewModel.deleteDownload(download.id) },
                    onPlayClick = {
                        val intent = Intent(context, PlayerActivity::class.java).apply {
                            data = Uri.parse(download.url) // Pass the video URI/URL
                            action = Intent.ACTION_VIEW // Handle it as a video action
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun DownloadItemRow(
    download: DownloadItem,
    onPauseClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column {
            Text(
                text = download.url,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = poppins,
                color = Color.White
            )
            LinearProgressIndicator(
                progress = { download.progress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF892CDC),
                trackColor = Color.Gray
            )
            Text(
                text = "${download.progress}% - ${downloadStatusToString(download.status)}",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = poppins,
                color = Color.White
            )
        }

        Row {
            // Conditionally display the pause or resume icon based on download status
            IconButton(
                onClick = onPauseClick
            ) {
                if (download.status == DownloadManager.STATUS_RUNNING) {
                    // Show pause icon when download is running
                    Icon(
                        painter = painterResource(id = R.drawable.pause_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Pause",
                        tint = Color.White
                    )
                } else if (download.status == DownloadManager.STATUS_PAUSED || download.status == DownloadManager.STATUS_FAILED) {
                    // Show resume icon when download is paused or failed
                    Icon(
                        painter = painterResource(id = R.drawable.play_arrow_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Resume",
                        tint = Color.White
                    )
                }
            }

            // Delete button
            IconButton(
                onClick = onDeleteClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }

            // Optionally show play button only when download is successful (to start playback)
            if (download.status == DownloadManager.STATUS_SUCCESSFUL) {
                IconButton(
                    onClick = onPlayClick,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.play_arrow_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


fun downloadStatusToString(status: Int): String {
    return when (status) {
        DownloadManager.STATUS_PENDING -> "Pending"
        DownloadManager.STATUS_RUNNING -> "Downloading"
        DownloadManager.STATUS_PAUSED -> "Paused"
        DownloadManager.STATUS_SUCCESSFUL -> "Completed"
        DownloadManager.STATUS_FAILED -> "Failed"
        else -> "Unknown"
    }
}