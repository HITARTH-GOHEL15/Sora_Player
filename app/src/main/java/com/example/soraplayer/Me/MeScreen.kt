package com.example.soraplayer.Me

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soraplayer.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MeScreen(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier
) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh,
            modifier = Modifier
                .padding(top = 120.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Iconitem(
                        onItemClick = { /*TODO*/ },
                        description = "Download",
                        painter = painterResource(id = R.drawable.download_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        iconname = "Download",
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.padding(24.dp))
                    Iconitem(
                        onItemClick = { /*TODO*/ },
                        description = "Stream",
                        painter = painterResource(id = R.drawable.stream_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        iconname = "Network Stream",
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.padding(24.dp))
                    Iconitem(
                        onItemClick = { /*TODO*/ },
                        description = "Explore",
                        painter = painterResource(id = R.drawable.explore_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        iconname = "Explore",
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.padding(top = 24.dp))
                Iconitem(
                    onItemClick = { /*TODO*/ },
                    description = "Setting",
                    painter = painterResource(id = R.drawable.settings_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    iconname = "Settings",
                    modifier = Modifier
                )
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