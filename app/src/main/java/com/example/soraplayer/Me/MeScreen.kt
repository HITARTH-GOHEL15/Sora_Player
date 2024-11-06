package com.example.soraplayer.Me

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soraplayer.ExploreScreen.ExploreScreen
import com.example.soraplayer.R
import com.example.soraplayer.download_screen.DownloadsScreen
import com.example.soraplayer.setting_screen.SettingsScreen
import com.example.soraplayer.setting_screen.SettingsViewModel
import com.example.soraplayer.setting_screen.SettingsViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MeScreen(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,

    modifier: Modifier
) {

    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val showDownloads = remember { mutableStateOf(false) }
    val showSettings = remember { mutableStateOf(false) }
    val showExplore = remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .padding(6.dp)
            .fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh,
            modifier = Modifier
                .padding(top = 130.dp, bottom = 100.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Iconitem(
                    onItemClick = {
                        showDownloads.value = true
                    },
                    description = "Download",
                    painter = painterResource(id = R.drawable.download_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    iconname = "Download",
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.weight(1f))
                Iconitem(
                    onItemClick = {
                        showExplore.value = true
                    },
                    description = "Explore",
                    painter = painterResource(id = R.drawable.explore_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    iconname = "Explore",
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.weight(1f))
                Iconitem(
                    onItemClick = {
                        showSettings.value = true
                    },
                    description = "Setting",
                    painter = painterResource(id = R.drawable.settings_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    iconname = "Settings",
                    modifier = Modifier
                )
            }
        }
    }
    if (showDownloads.value) {
        Dialog(onDismissRequest = { showDownloads.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF222831), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                DownloadsScreen(
                    context = context,
                    modifier = Modifier.background(color = Color(0xFF222831))
                )// Integrate the DownloadsScreen here
                Button(
                    onClick = { showDownloads.value = false },
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF892CDC),
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
    // Settings Dialog
    if (showSettings.value && activity != null) {
        Dialog(onDismissRequest = { showSettings.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // Initialize the SettingsViewModel using a factory
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(context)
                )

                // Pass ViewModel and Activity to SettingsScreen
                SettingsScreen(
                    viewModel = viewModel,
                    activity = activity,
                    context = context
                )

                Button(
                    onClick = { showSettings.value = false },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text("Close")
                }
            }
        }
    }
    if (showExplore.value) {
        Dialog(onDismissRequest = { showExplore.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background( Color(0xFF222831), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                // Call ExploreScreen
                ExploreScreen(context)

                Button(
                    onClick = { showExplore.value = false },
                    modifier = Modifier.align(Alignment.BottomEnd),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF892CDC),
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
        }





@Composable
fun Iconitem(
    onItemClick: () -> Unit,
    description: String,
    painter: Painter,
    iconname: String,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .clickable(
                onClick = {
                    onItemClick()
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painter,
            tint = Color(0xFF892CDC),
            contentDescription = description,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = iconname,
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