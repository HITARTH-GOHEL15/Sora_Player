package com.example.soraplayer.Presentation.UI.VideoScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.soraplayer.Data.BottomNavigationItem
import com.example.soraplayer.R
import com.example.soraplayer.ui.theme.poppins
import org.videolan.BuildConfig

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RequestMediaPermission(viewModel: PermissionViewModel) {
    val mediaPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.setPermissionGranted(isGranted)

        }
    )

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Filled.Home,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Music",
            selectedIcon =  Icons.Filled.PlayArrow,
            unselectedIcon = Icons.Filled.PlayArrow,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Account",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon =  Icons.Filled.AccountCircle,
            hasNews = false,
        ),
    )

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold(
//        topBar = {
//            androidx.compose.material3.TopAppBar(
//                title = {
//                    Box(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentAlignment = Alignment.TopStart
//                    ) {
//                        Row  {
//                            Text(
//                                text = "Sora",
//                                fontFamily = poppins,
//                                color = MaterialTheme.colorScheme.primary,
//                                fontWeight = FontWeight.ExtraBold,
//                            )
//                            Text(
//                                text = "Player",
//                                fontFamily = poppins,
//                                fontWeight = FontWeight.ExtraBold,
//                            )
//                        }
//                    }
//                },
//                actions = {
//                    IconButton(
//                        onClick = { /*TODO*/ },
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Search,
//                            contentDescription = "Search",
//                            tint = MaterialTheme.colorScheme.onBackground
//                        )
//                    }
//                    IconButton(
//                        onClick = { /*TODO*/ },
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.folder_24dp_e8eaed_fill0_wght400_grad0_opsz24),
//                            contentDescription = "File",
//                            tint = MaterialTheme.colorScheme.onBackground
//                        )
//
//                    }
//                    IconButton(
//                        onClick = { /*TODO*/ },
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.settings_24dp_e8eaed_fill0_wght400_grad0_opsz24),
//                            contentDescription = "setting",
//                            tint = MaterialTheme.colorScheme.onBackground
//
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Transparent,
//                ),
//
//                )
//        },
//        bottomBar = {
//            NavigationBar(
//                containerColor = Color.Transparent,
//            ) {
//                // Bottom navigation items
//                items.forEachIndexed { index, bottomNavigationItem ->
//                    NavigationBarItem(
//                        selected = selectedItemIndex == index,
//                        onClick = {
//                            selectedItemIndex = index
//                            // Handle item click
//                        },
//                        icon = {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Icon(
//                                    imageVector = if (index == selectedItemIndex) {
//                                        bottomNavigationItem.selectedIcon
//                                    } else {
//                                        bottomNavigationItem.unselectedIcon
//                                    },
//                                    contentDescription = bottomNavigationItem.title,
//                                    tint = MaterialTheme.colorScheme.onBackground
//                                )
//                            }
//                            Box(
//                                modifier = Modifier.fillMaxWidth(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    text = bottomNavigationItem.title,
//                                    modifier = Modifier.padding(top = 24.dp)
//                                )
//                            }
//
//                        },
//
//                        )
//                }
//
//            }
//        },
       ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                arrayOf(mediaPermissionResultLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO))
                mediaPermissionResultLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
            }) {
                Text(text = "Request one permission")
            }
        }
    }
}