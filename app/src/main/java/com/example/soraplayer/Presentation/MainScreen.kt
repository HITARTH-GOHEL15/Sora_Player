package com.example.soraplayer.Presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soraplayer.MainScreen.MainViewModel
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.Presentation.mainScreenComponents.FolderItemGridLayout
import com.example.soraplayer.Presentation.mainScreenComponents.VideoItemGridLayout
import com.example.soraplayer.R
import com.example.soraplayer.ui.theme.poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onVideoItemClick: (VideoItem) -> Unit
){

    var bottomNavigationScreen by rememberSaveable {
        mutableStateOf( BottomNavigationScreens.VideosView )
    }

    val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)

    val videosViewStateFlow by mainViewModel.videoItemsStateFlow.collectAsState()
    val foldersViewStateFlow by mainViewModel.folderItemStateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
              title = {
                  Row {
                      Text(
                          text = "Sora",
                          fontFamily = poppins,
                          color = MaterialTheme.colorScheme.primary,
                          style = MaterialTheme.typography.titleLarge
                      )
                      Text(
                          text = "Player",
                          fontFamily = poppins,
                          style = MaterialTheme.typography.titleLarge
                      )
                  }
              },
              actions = {
                  Icon(
                      imageVector = Icons.Filled.Search,
                      contentDescription = "Search",
                      tint = MaterialTheme.colorScheme.onBackground,
                  )
                  Spacer(modifier = Modifier.padding(6.dp))
                  Icon(
                      painter = painterResource(R.drawable.sort_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                      contentDescription = "Sort",
                      tint = MaterialTheme.colorScheme.onBackground,
                      )
                  Spacer(modifier = Modifier.padding(6.dp))
                  Icon(
                      painter = painterResource(R.drawable.settings_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                      contentDescription = "setting",
                      tint = MaterialTheme.colorScheme.onBackground,
                      modifier = Modifier.padding(end = 2.dp)
                  )
              },
                colors = TopAppBarDefaults.topAppBarColors(
                    Color.Transparent
                ),
                modifier = Modifier

          )

        },
        bottomBar = {
            NavigationBar(
                tonalElevation = 12.dp,
                containerColor = Color.Transparent
            ){
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    selected = bottomNavigationScreen == BottomNavigationScreens.VideosView,
                    label = { Text(text = stringResource(id = R.string.videos_layout)) },
                    onClick = {
                        bottomNavigationScreen = BottomNavigationScreens.VideosView
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.video_library_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(id = R.string.videos_layout)
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    selected = bottomNavigationScreen == BottomNavigationScreens.FoldersView,
                    label = { Text(text = stringResource(id = R.string.folders_layout)) },
                    onClick = {
                        bottomNavigationScreen = BottomNavigationScreens.FoldersView
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.perm_media_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(id = R.string.folders_layout)
                        )
                    }
                )
            }
        }
    ) { paddingValues ->

        AnimatedContent(
            targetState = bottomNavigationScreen ,
            label = "",
            transitionSpec = {
                when(this.targetState){
                    BottomNavigationScreens.VideosView -> slideInHorizontally(){-it}.togetherWith(slideOutHorizontally(){it})

                    BottomNavigationScreens.FoldersView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                }
            }
        ) { navScreen ->
            when(navScreen){

                BottomNavigationScreens.VideosView -> {

                    VideoItemGridLayout(
                        contentPadding = paddingValues,
                        videoList = videosViewStateFlow,
                        onVideoItemClick = onVideoItemClick,
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.background)
                            .padding(bottom = 75.dp)
                    )

                }
                BottomNavigationScreens.FoldersView -> {

                    var foldersVideosNavigation by rememberSaveable{
                        mutableStateOf(FoldersVideosNavigation.FoldersScreen)
                    }

                    Crossfade(
                        targetState = foldersVideosNavigation, label = "",
                        animationSpec = tween(300, easing = LinearEasing)
                    ) { foldersAndVideosNav ->

                        when(foldersAndVideosNav){

                            FoldersVideosNavigation.FoldersScreen -> {

                                FolderItemGridLayout(
                                    foldersList = foldersViewStateFlow,
                                    onFolderItemClick = {
                                        mainViewModel.updateCurrentSelectedFolderItem(it)
                                        foldersVideosNavigation = FoldersVideosNavigation.VideosScreen
                                    },
                                    contentPadding = paddingValues
                                )

                            }

                            FoldersVideosNavigation.VideosScreen -> {

                                BackHandler(true) {
                                    foldersVideosNavigation = FoldersVideosNavigation.FoldersScreen
                                }
                                VideoItemGridLayout(
                                    contentPadding = paddingValues,
                                    videoList = mainViewModel.currentSelectedFolder.videoItems,
                                    onVideoItemClick = onVideoItemClick,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class BottomNavigationScreens{
    VideosView,
    FoldersView
}

private enum class FoldersVideosNavigation {
    FoldersScreen,
    VideosScreen
}