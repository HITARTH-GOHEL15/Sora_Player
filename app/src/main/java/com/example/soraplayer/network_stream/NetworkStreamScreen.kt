package com.example.soraplayer.network_stream

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NetworkStreamScreen(
    onPlayStream: (String) -> Unit,
) {
    var url by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 120.dp),
    ) {
        // TextField to enter the URL
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text(
                "Enter Video URL",
                color = Color.White
            ) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF892CDC),
                unfocusedIndicatorColor = Color.Transparent,
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

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNetworkStreamScreen() {
    NetworkStreamScreen(
        onPlayStream = {},
    )
}
