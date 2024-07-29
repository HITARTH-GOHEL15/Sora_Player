package com.example.soraplayer.MainScreen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soraplayer.Model.VideoItem
import com.example.soraplayer.Presentation.mainScreenComponents.FolderItemGridLayout
import com.example.soraplayer.Presentation.mainScreenComponents.FolderItemListLayout
import com.example.soraplayer.Presentation.mainScreenComponents.VideoItemGridLayout
import com.example.soraplayer.Presentation.mainScreenComponents.VideoViewList
import com.example.soraplayer.R
import com.example.soraplayer.ui.theme.poppins
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onVideoItemClick: (VideoItem) -> Unit
){
    val layoutDirection = LocalLayoutDirection.current

    var bottomNavigationScreen by rememberSaveable {
        mutableStateOf(BottomNavigationScreens.VideosView)
    }

    val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)

    val videosViewStateFlow by mainViewModel.videoItemsStateFlow.collectAsState()
    val foldersViewStateFlow by mainViewModel.folderItemStateFlow.collectAsState()

    var sortOrder by rememberSaveable {
        mutableStateOf(SortOrder.Name)
    }
    var sortDirection by rememberSaveable { mutableStateOf(SortDirection.Ascending) }

    val ListState = rememberLazyListState()
    val gridState = rememberLazyStaggeredGridState()

    var isGridLayout by rememberSaveable {
        mutableStateOf(true)
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
                                    fontFamily = poppins
                                )
                                          },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF049FFD),
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color(0xFF049FFD),
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {isSearching = false}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { searchQuery = ""}) {
                            Icon(Icons.Default.Close , contentDescription = "Clear")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
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
                                color = Color(0xFF049FFD),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Player",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
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
                                tint = MaterialTheme.colorScheme.onBackground,
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
                            isGridLayout = isGridLayout,
                            onLayoutChange = { isGridLayout = it },
                            modifier = Modifier
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        Color.Transparent

                        ),

                )
            }

        },
        bottomBar = {
            NavigationBar(
                tonalElevation = 12.dp,
                containerColor = Color.Transparent
            ){
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF049FFD)
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
                        indicatorColor = Color(0xFF049FFD)
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
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    selected = false,
                    label = { Text(text = "Music") },
                    onClick = { /*TODO*/ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.music_note_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = "Music"
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    selected = false,
                    label = { Text(text = "Me") },
                    onClick = { /*TODO*/ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.person_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = "Me"
                        )
                    }
                )
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
                }
            },
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
        ) { navScreen ->
            LaunchedEffect(sortOrder, sortDirection, bottomNavigationScreen) {
                if (isGridLayout) {
                    gridState.scrollToItem(0)
                } else {
                    ListState.scrollToItem(0)
                }
            }

                when (navScreen) {

                    BottomNavigationScreens.VideosView -> {
                        if (isGridLayout) {
                            VideoItemGridLayout(
                                videoList = filteredVideos,
                                onVideoItemClick = onVideoItemClick,
                                scrollState = gridState,
                                isRefreshing = isRefreshing,
                                onRefresh = { mainViewModel.refreshData() },
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.background)
                                    .padding(top = 70.dp, bottom = 120.dp),
                            )
                        } else {
                            VideoViewList(
                                videoList = filteredVideos,
                                onVideoItemClick = onVideoItemClick,
                                scrollState = ListState,
                                isRefreshing = isRefreshing,
                                onRefresh = { mainViewModel.refreshData() },
                                modifier = Modifier.
                                    background(color = MaterialTheme.colorScheme.background)
                                    .padding(top = 70.dp, bottom = 120.dp),
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
                                .background(color = MaterialTheme.colorScheme.background)
                        ) { foldersAndVideosNav ->

                            when (foldersAndVideosNav) {

                                FoldersVideosNavigation.FoldersScreen -> {

                                    if(isGridLayout) {
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
                                                .background(color = MaterialTheme.colorScheme.background)
                                                .padding(top = 50.dp, bottom = 120.dp)
                                        )
                                    } else {
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
                                                .background(color = MaterialTheme.colorScheme.background)
                                                .padding(top = 50.dp, bottom = 120.dp)
                                        )
                                    }

                                }

                                FoldersVideosNavigation.VideosScreen -> {

                                    BackHandler(true) {
                                        foldersVideosNavigation =
                                            FoldersVideosNavigation.FoldersScreen
                                    }
                                    if (isGridLayout) {
                                        VideoItemGridLayout(
                                            videoList = filteredFolderVideos,
                                            onVideoItemClick = onVideoItemClick,
                                            scrollState = gridState,
                                            isRefreshing = isRefreshing,
                                            onRefresh = { mainViewModel.refreshData() },
                                            modifier = Modifier
                                                .padding(top = 70.dp,bottom = 120.dp)
                                                .background(color = MaterialTheme.colorScheme.background)
                                        )
                                    } else {
                                        VideoViewList(
                                            videoList = filteredFolderVideos,
                                            onVideoItemClick = onVideoItemClick,
                                            scrollState = ListState,
                                            isRefreshing = isRefreshing,
                                            onRefresh = { mainViewModel.refreshData() },
                                            modifier = Modifier
                                                .padding(top = 70.dp,bottom = 120.dp)
                                                .background(color = MaterialTheme.colorScheme.background)
                                        )
                                    }
                                }
                            }
                        }
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
                   tint = MaterialTheme.colorScheme.onBackground,
               )
           }
           DropdownMenu(
               expanded = expanded,
               onDismissRequest = {expanded = false},
               modifier = Modifier
                   .focusRequester(focusRequester)
                   .background(color = MaterialTheme.colorScheme.background)
           ) {
               DropdownMenuItem(
                   text = {
                       Text(
                           text = "Sort by Name"
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
                           text = "Sort by Date"
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
                           text = "Sort by Size"
                       )
                   },
                   onClick = {
                       onSortOrderChange(SortOrder.Size)
                       expanded = false
                   }
               )
               DropdownMenuItem(
                   onClick = {
                       onSortDirectionChanged(SortDirection.Ascending)
                   },
                   text = {
                       Text(
                           "Ascending"
                       )
                   }
               )
               DropdownMenuItem(
                   onClick = {
                       onSortDirectionChanged(SortDirection.Descending)
                   },
                   text = {
                       Text(
                           "Descending"
                       )
                   }
               )
           }
       }


@Composable
fun LayoutChange(
    isGridLayout: Boolean,
    onLayoutChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var focusRequester = remember {
        FocusRequester()
    }

    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = painterResource(R.drawable.view_list_24dp_e8eaed_fill0_wght400_grad0_opsz24),
            contentDescription = "Layout",
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .focusRequester(focusRequester)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    if (isGridLayout) {
                        "Switch to List View"
                    } else {
                        "Switch to Grid View"
                    }
                )
            },
            onClick = {
                onLayoutChange(!isGridLayout)
                expanded = false
            }
        )
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

private enum class SortOrder {
    Name,Date,Size
}

private enum class SortDirection {
    Ascending , Descending
}