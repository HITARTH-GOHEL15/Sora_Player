package com.example.soraplayer.MainScreen

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.soraplayer.Me.MeScreen
import com.example.soraplayer.Model.MinimizedPlayerState
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.MusicPlayer.MusicPlayerViewModel
import com.example.soraplayer.Player.PlayerViewModel
import com.example.soraplayer.Presentation.mainScreenComponents.FolderItemGridLayout
import com.example.soraplayer.Presentation.mainScreenComponents.FolderItemListLayout
import com.example.soraplayer.Presentation.mainScreenComponents.MusicGrid
import com.example.soraplayer.Presentation.mainScreenComponents.MusicList
import com.example.soraplayer.Presentation.mainScreenComponents.VideoItemGridLayout
import com.example.soraplayer.Presentation.mainScreenComponents.VideoViewList
import com.example.soraplayer.R
import com.example.soraplayer.network_stream.NetworkStreamScreen
import com.example.soraplayer.network_stream.RecentUrlManager
import com.example.soraplayer.ui.theme.poppins

@androidx.annotation.OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onVideoItemClick: (VideoItem) -> Unit,
    onPlayStream: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    val layoutDirection = LocalLayoutDirection.current

    var bottomNavigationScreen by rememberSaveable {
        mutableStateOf(BottomNavigationScreens.VideosView)
    }

    val context = LocalContext.current

    val musicPlayerViewModel: MusicPlayerViewModel = viewModel(factory = MusicPlayerViewModel.factory)
    val playerViewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.factory)

    val recentUrlManager = remember { RecentUrlManager(context) }

    val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)

    val videosViewStateFlow by mainViewModel.videoItemsStateFlow.collectAsState()
    val foldersViewStateFlow by mainViewModel.folderItemStateFlow.collectAsState()
    val musicViewStateFlow by mainViewModel.musicItemsStateFlow.collectAsState()

    var sortOrder by rememberSaveable {
        mutableStateOf(SortOrder.Date)
    }
    var sortDirection by rememberSaveable { mutableStateOf(SortDirection.Descending) }

    val ListState = rememberLazyListState()
    val gridState = rememberLazyStaggeredGridState()

    var isListLayout by rememberSaveable {
        mutableStateOf(true)
    }

    val onLayoutChange: (Boolean) -> Unit = { newLayout ->
        isListLayout = newLayout
    }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val isRefreshing by mainViewModel.isRefreshing.collectAsState()

    val filteredVideos by remember(searchQuery, sortOrder, sortDirection) {
        derivedStateOf {
            val sortedList = when (sortOrder) {
                SortOrder.Name -> videosViewStateFlow.sortedBy { it.name }
                SortOrder.Date -> videosViewStateFlow.sortedBy { it.dateModified }
                SortOrder.Size -> videosViewStateFlow.sortedBy { it.size }

            }
            val finalList = if (sortDirection == SortDirection.Descending) sortedList.reversed() else sortedList
            if (searchQuery.isNotBlank()) {
                finalList.filter { it.name.contains(searchQuery, ignoreCase = true) }
            } else {
                finalList
            }
        }
    }

    val filteredTracks by remember(searchQuery , sortOrder , sortDirection) {
        derivedStateOf {
            val sortedList = when(sortOrder){
                SortOrder.Name -> musicViewStateFlow.sortedBy { it.name }
                SortOrder.Date -> musicViewStateFlow.sortedBy { it.dateModified }
                SortOrder.Size -> musicViewStateFlow.sortedBy { it.size }
            }
            val finalList = if(sortDirection == SortDirection.Descending) sortedList.reversed() else sortedList
            if (searchQuery.isNotBlank()) {
                finalList.filter { it.name.contains(searchQuery, ignoreCase = true) }
            } else {
                finalList
            }
        }
    }

    val filteredFolderVideos by remember(searchQuery, sortOrder, sortDirection, mainViewModel.currentSelectedFolder) {
        derivedStateOf {
            val sortedList = when (sortOrder) {
                SortOrder.Name -> mainViewModel.currentSelectedFolder.videoItems.sortedBy { it.name }
                SortOrder.Date -> mainViewModel.currentSelectedFolder.videoItems.sortedBy { it.dateModified }
                SortOrder.Size -> mainViewModel.currentSelectedFolder.videoItems.sortedBy { it.size }
            }
            val finalList = if (sortDirection == SortDirection.Descending) sortedList.reversed() else sortedList
            if (searchQuery.isNotBlank()) {
                finalList.filter { it.name.contains(searchQuery, ignoreCase = true) }
            } else {
                finalList
            }
        }
    }

    val filteredFolders by remember(searchQuery , sortOrder , sortDirection , mainViewModel.folderItemStateFlow) {
        derivedStateOf {
            val sortedList = when (sortOrder) {
                SortOrder.Name -> foldersViewStateFlow.sortedBy { it.name }
                SortOrder.Size -> foldersViewStateFlow.sortedBy { it.videoItems.size }
                SortOrder.Date -> foldersViewStateFlow.sortedBy { it.videoItems.first().dateModified }
            }
            val finalList = if (sortDirection == SortDirection.Descending) sortedList.reversed() else sortedList
            if (searchQuery.isNotBlank()) {
                finalList.filter { it.name.contains(searchQuery, ignoreCase = true) }
            } else {
                finalList
            }
        }
    }


    Scaffold(
        topBar = {
            if(isSearching) {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "Search",
                                    fontFamily = poppins,
                                    color =  MaterialTheme.colorScheme.onBackground
                                )
                                          },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF892CDC),
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color(0xFF892CDC),
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {isSearching = false}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFFD9ACF5)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { searchQuery = ""}) {
                            Icon(
                                Icons.Default.Close ,
                                contentDescription = "Clear",
                                tint = Color(0xFFD9ACF5)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF222831)
                    ),

                    )
            } else {
                TopAppBar(
                    title = {
                        Row {
                            Text(
                                text = "Sora",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF892CDC),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Player",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { isSearching = true }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color(0xFFD9ACF5),
                            )
                        }

                        Spacer(modifier = Modifier.padding(6.dp))
                        SortAndLayoutDropdownMenu(
                            sortOrder = sortOrder,
                            sortDirection = sortDirection,
                            onSortOrderChange = { newSortOrder ->
                                sortOrder = newSortOrder
                            },
                            onSortDirectionChanged = { newSortDirection ->
                                sortDirection = newSortDirection
                            },
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        LayoutChange(
                            isListLayout = isListLayout,
                            onLayoutChange =  onLayoutChange ,
                            modifier = Modifier
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF222831)
                        ),
                )
            }

        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                NavigationBar(
                    tonalElevation = 12.dp,
                    containerColor = Color(0xFF222831),
                    modifier = Modifier
                        .border(
                            width = 0.2.dp,
                            color = Color.DarkGray,
                        )
                        .shadow(
                            elevation = 12.dp,
                            spotColor = Color(0xFF892CDC),
                            ambientColor = Color(0xFF892CDC)
                        )
                ) {
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF892CDC)
                        ),
                        selected = bottomNavigationScreen == BottomNavigationScreens.VideosView,
                        label = {
                            Text(
                                text = stringResource(id = R.string.videos_layout),
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEEEEEE)
                            )
                        },
                        onClick = {
                            bottomNavigationScreen = BottomNavigationScreens.VideosView
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.video_library_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                contentDescription = stringResource(id = R.string.videos_layout),
                                tint = Color(0xFFD9ACF5)
                            )
                        }
                    )
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF892CDC)
                        ),
                        selected = bottomNavigationScreen == BottomNavigationScreens.FoldersView,
                        label = {
                            Text(
                                text = stringResource(id = R.string.folders_layout),
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEEEEEE)
                            )
                        },
                        onClick = {
                            bottomNavigationScreen = BottomNavigationScreens.FoldersView
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.perm_media_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                contentDescription = stringResource(id = R.string.folders_layout),
                                tint = Color(0xFFD9ACF5)
                            )
                        }
                    )
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF892CDC)
                        ),
                        selected = bottomNavigationScreen == BottomNavigationScreens.MusicView,
                        label = {
                            Text(
                                text = "Music",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEEEEEE)
                            )
                        },
                        onClick = {
                            bottomNavigationScreen = BottomNavigationScreens.MusicView
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.music_note_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                contentDescription = "Music",
                                tint = Color(0xFFD9ACF5)
                            )
                        }
                    )
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF892CDC)
                        ),
                        selected = bottomNavigationScreen == BottomNavigationScreens.NetworkStreamView,
                        label = {
                            Text(
                                text = "Stream",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEEEEEE)
                            )
                        },
                        onClick = {
                            bottomNavigationScreen = BottomNavigationScreens.NetworkStreamView
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.stream_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                contentDescription = "Network Stream",
                                tint = Color(0xFFD9ACF5)
                            )
                        }
                    )
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF892CDC)
                        ),
                        selected = false,
                        label = {
                            Text(
                                text = "Me",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEEEEEE)
                            )
                        },
                        onClick = {
                            bottomNavigationScreen = BottomNavigationScreens.MeView
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.person_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                contentDescription = "Me",
                                tint = Color(0xFFD9ACF5)
                            )
                        }
                    )
                }
            }
        }
    ) {
        AnimatedContent(
            targetState = bottomNavigationScreen ,
            label = "",
            transitionSpec = {
                when(this.targetState){
                    BottomNavigationScreens.VideosView -> slideInHorizontally(){-it}.togetherWith(slideOutHorizontally(){it})

                    BottomNavigationScreens.FoldersView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                    BottomNavigationScreens.MusicView ->  slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                    BottomNavigationScreens.MeView -> slideInHorizontally(){-it}.togetherWith(slideOutHorizontally(){it})
                    BottomNavigationScreens.NetworkStreamView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                }
            },
            modifier = Modifier
                .background(color = Color(0xFF222831))
        ) { navScreen ->
            LaunchedEffect(sortOrder, sortDirection, bottomNavigationScreen) {
                if (isListLayout) {
                    gridState.scrollToItem(0)
                } else {
                    ListState.scrollToItem(0)
                }
            }

                when (navScreen) {

                    BottomNavigationScreens.VideosView -> {
                        if (isListLayout) {
                            VideoViewList(
                                videoList = filteredVideos,
                                onVideoItemClick = onVideoItemClick,
                                scrollState = ListState,
                                isRefreshing = isRefreshing,
                                onRefresh = { mainViewModel.refreshData() },
                                modifier = Modifier
                                    .background(color = Color(0xFF222831))
                            )
                        } else {
                            VideoItemGridLayout(
                                videoList = filteredVideos,
                                onVideoItemClick = onVideoItemClick,
                                scrollState = gridState,
                                isRefreshing = isRefreshing,
                                onRefresh = { mainViewModel.refreshData() },
                                modifier = Modifier
                                    .background(color = Color(0xFF222831))
                                    .padding(top = 24.dp)
                            )
                        }

                    }

                    BottomNavigationScreens.FoldersView -> {

                        var foldersVideosNavigation by rememberSaveable {
                            mutableStateOf(FoldersVideosNavigation.FoldersScreen)
                        }

                        Crossfade(
                            targetState = foldersVideosNavigation, label = "",
                            animationSpec = tween(300, easing = LinearEasing),
                            modifier = Modifier
                                .background(color = Color(0xFF222831))
                        ) { foldersAndVideosNav ->

                            when (foldersAndVideosNav) {

                                FoldersVideosNavigation.FoldersScreen -> {

                                    if(isListLayout) {
                                        FolderItemListLayout(
                                            foldersList = filteredFolders,
                                            onFolderItemClick = {
                                                mainViewModel.updateCurrentSelectedFolderItem(it)
                                                foldersVideosNavigation =
                                                    FoldersVideosNavigation.VideosScreen
                                            },
                                            isRefreshing = isRefreshing,
                                            onRefresh = { mainViewModel.refreshData() },
                                            modifier = Modifier
                                                .background(color = Color(0xFF222831))
                                                .padding(top = 50.dp),
                                        )
                                    } else {
                                        FolderItemGridLayout(
                                            foldersList = filteredFolders,
                                            onFolderItemClick = {
                                                mainViewModel.updateCurrentSelectedFolderItem(it)
                                                foldersVideosNavigation =
                                                    FoldersVideosNavigation.VideosScreen
                                            },
                                            isRefreshing = isRefreshing,
                                            onRefresh = { mainViewModel.refreshData() },
                                            modifier = Modifier
                                                .background(color = Color(0xFF222831))
                                                .padding(top = 50.dp)
                                        )
                                    }

                                }

                                FoldersVideosNavigation.VideosScreen -> {

                                    BackHandler(true) {
                                        foldersVideosNavigation =
                                            FoldersVideosNavigation.FoldersScreen
                                    }
                                    if (isListLayout) {
                                        VideoViewList(
                                            videoList = filteredFolderVideos,
                                            onVideoItemClick = onVideoItemClick,
                                            scrollState = ListState,
                                            isRefreshing = isRefreshing,
                                            onRefresh = { mainViewModel.refreshData() },
                                            modifier = Modifier
                                                .background(color = Color(0xFF222831))
                                                .padding(top = 44.dp)
                                        )
                                    } else {
                                        VideoItemGridLayout(
                                            videoList = filteredFolderVideos,
                                            onVideoItemClick = onVideoItemClick,
                                            scrollState = gridState,
                                            isRefreshing = isRefreshing,
                                            onRefresh = { mainViewModel.refreshData() },
                                            modifier = Modifier
                                                .background(color = Color(0xFF222831))
                                                .padding(top = 24.dp)
                                        )
                                    }
                                }
                            }

                        }
                    }
                    BottomNavigationScreens.MusicView -> {
                        if (isListLayout) {
                            MusicList(
                                musicTracks = filteredTracks,
                                modifier = Modifier
                                    .padding(top = 35.dp)
                            )
                            } else {
                            MusicGrid(
                                musicTracks = filteredTracks,
                                modifier = Modifier
                                    .padding(top = 35.dp)
                            )
                        }
                    }
                    BottomNavigationScreens.NetworkStreamView -> {
                        NetworkStreamScreen(
                            onPlayStream = onPlayStream,
                            recentUrlManager = recentUrlManager,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color(0xFF222831))
                        )
                    }
                    BottomNavigationScreens.MeView -> {
                        MeScreen(
                            isRefreshing = isRefreshing,
                            onRefresh = { mainViewModel.refreshData() },
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color(0xFF222831) ),
                        )
                    }
                }
            }
        }
            }




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortAndLayoutDropdownMenu(
    sortOrder: SortOrder,
    sortDirection: SortDirection,
    onSortOrderChange: (SortOrder) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var focusRequester = remember {
        FocusRequester()
    }
           IconButton(onClick = { expanded = true }) {
               Icon(
                   painter = painterResource(R.drawable.sort_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                   contentDescription = "Sort",
                   tint = Color(0xFFD9ACF5),
               )
           }
           DropdownMenu(
               expanded = expanded,
               onDismissRequest = {expanded = false},
               modifier = Modifier
                   .focusRequester(focusRequester)
                   .background(color = Color(0xFF222831))
           ) {
               DropdownMenuItem(
                   text = {
                       Text(
                           text = "Sort by Name",
                           color = Color(0xFFD9ACF5)
                       )
                   },
                   onClick = {
                       onSortOrderChange(SortOrder.Name)
                       expanded = false
                   }
               )
               DropdownMenuItem(
                   text = {
                       Text(
                           text = "Sort by Date",
                           color = Color(0xFFD9ACF5)
                       )
                   },
                   onClick = {
                       onSortOrderChange(SortOrder.Date)
                       expanded = false
                   },
               )
               DropdownMenuItem(
                   text = {
                       Text(
                           text = "Sort by Size",
                           color = Color(0xFFD9ACF5)
                       )
                   },
                   onClick = {
                       onSortOrderChange(SortOrder.Size)
                       expanded = false
                   }
               )
               HorizontalDivider(
                   modifier = Modifier.padding(vertical = 4.dp),
                   thickness = 1.dp,
                   color = Color.Gray
               )

               DropdownMenuItem(
                   onClick = {
                       onSortDirectionChanged(SortDirection.Ascending)
                   },
                   text = {
                       Text(
                           "Ascending",
                           color = Color(0xFFD9ACF5)
                       )
                   }
               )
               DropdownMenuItem(
                   onClick = {
                       onSortDirectionChanged(SortDirection.Descending)
                   },
                   text = {
                       Text(
                           "Descending",
                           color = Color(0xFFD9ACF5)
                       )
                   }
               )
           }
}


@Composable
fun LayoutChange(
    isListLayout: Boolean,
    onLayoutChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    IconButton(onClick = { onLayoutChange(!isListLayout) }) {
        Icon(
            painter =  painterResource(
                if (isListLayout) {
                    R.drawable.grid_view_24dp_e8eaed_fill0_wght400_grad0_opsz24// List icon
                } else {
                    R.drawable.view_list_24dp_e8eaed_fill0_wght400_grad0_opsz24 // Replace with your grid icon resource
                }
            ),
            contentDescription = "Layout",
            tint = Color(0xFFD9ACF5),
        )
    }

}



private enum class BottomNavigationScreens{
    VideosView,
    FoldersView,
    MusicView,
    NetworkStreamView,
    MeView
}

private enum class FoldersVideosNavigation {
    FoldersScreen,
    VideosScreen
}

private enum class SortOrder {
    Name,Date,Size
}

private enum class SortDirection {
    Ascending , Descending
}