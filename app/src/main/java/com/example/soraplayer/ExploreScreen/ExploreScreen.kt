package com.example.soraplayer.ExploreScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.soraplayer.ui.theme.poppins

@Composable
fun ExploreScreen(context: Context) {
    val viewModel: ExploreViewModel = viewModel()
    val exploreItems = viewModel.exploreItems.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value
    val loading = viewModel.loading.collectAsState().value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(color = Color(0xFF222831))) {

        Text(
            text = "Explore",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF892CDC),
            modifier = Modifier.padding(bottom = 8.dp),
            fontFamily = poppins
        )
        // Show loading spinner
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            // Show error message
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        } else {
            // Display images in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(exploreItems.size) { index ->
                    val item = exploreItems[index]
                    ImageThumbnail(
                        imageUrl = item.imageUrl,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ImageThumbnail(imageUrl: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = "Image thumbnail",
            modifier = Modifier
                .height(170.dp)
                .width(400.dp)
                .fillMaxWidth()
                .background(shape = MaterialTheme.shapes.medium, color = Color(0xFF222831)),
            contentScale = ContentScale.Crop
        )
    }
}

