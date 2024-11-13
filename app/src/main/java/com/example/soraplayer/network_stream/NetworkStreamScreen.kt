package com.example.soraplayer.network_stream

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import com.example.soraplayer.ui.theme.poppins

@Composable
fun NetworkStreamScreen(
    onPlayStream: (String) -> Unit,
    recentUrlManager: RecentUrlManager,
    modifier: Modifier
) {
    var url by remember { mutableStateOf("") }

    val recentUrls = remember { mutableStateListOf<String>() }

    val context = LocalContext.current

    // Load recent URLs initially
    LaunchedEffect(Unit) {
        recentUrls.clear()
        recentUrls.addAll(recentUrlManager.getRecentUrls())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp),
    ) {
        // TextField to enter the URL
        OutlinedTextField(
            value = url,
            onValueChange = {
                url = it
                            },
            label = { Text(
                "Enter Video URL",
                color = Color.White
            ) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF892CDC),
                unfocusedIndicatorColor = Color(0xFF892CDC),
                cursorColor = Color(0xFF892CDC),
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Button to play the video from the URL
        OutlinedButton(
            onClick = {
                if (url.isNotEmpty()) {
                    onPlayStream(url)
                    recentUrlManager.saveUrl(url) // Save the URL to SharedPreferences
                    recentUrls.clear() // Refresh the recent URLs list
                    recentUrls.addAll(recentUrlManager.getRecentUrls())
                } else {
                    Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFF892CDC),
                contentColor = Color.White
            )
        ) {
           Text(
               text = "Play",
               color = Color.White
           )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(Color.DarkGray) // Color for the border line
        )

        // Recently used URLs section
        Text(
            text = "Recently Played URLs",
            color = Color(0xFF892CDC),
            fontFamily = poppins,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(recentUrls) { recentUrl ->
                Text(
                    text = recentUrl,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Set the clicked URL in the TextField
                            url = recentUrl
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }

    }
}



class RecentUrlManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Save a new URL to the list of recent URLs
    fun saveUrl(url: String) {
        val urls = getRecentUrls().toMutableSet()
        urls.add(url) // Add new URL to the set (avoiding duplicates)
        prefs.edit().putStringSet(KEY_RECENT_URLS, urls).apply()
    }

    // Retrieve all recent URLs as a list
    fun getRecentUrls(): List<String> {
        return prefs.getStringSet(KEY_RECENT_URLS, emptySet())?.toList() ?: emptyList()
    }

    companion object {
        private const val PREFS_NAME = "soraplayer_prefs"
        private const val KEY_RECENT_URLS = "recent_urls"
    }
}

