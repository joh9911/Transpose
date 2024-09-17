package com.example.transpose.ui.screen.home.playlist_item

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.ui.common.PaginatedState
import com.example.transpose.ui.components.dialog.AddVideoToPlaylistDialog
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.items.LoadingIndicator
import com.example.transpose.ui.screen.home.playlist_item.items.PlaylistHeaderItem
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePlaylistItemScreen(
    mainViewModel: MainViewModel,
    homePlaylistItemViewModel: HomePlaylistItemViewModel,
    navigationViewModel: NavigationViewModel,
    mediaViewModel: MediaViewModel,
    itemId: String?,
) {
    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val playlistInfo by homePlaylistItemViewModel.playlistInfo.collectAsState()
    val playlistItemsState by homePlaylistItemViewModel.playlistItemsState.collectAsState()
    val isShowingPlaylistDialog by mainViewModel.isShowAddVideoToPlaylistDialog.collectAsState()
    val myPlaylists by mainViewModel.myPlaylists.collectAsState()
    val selectedVideo by mainViewModel.selectedVideo.collectAsState()
    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }

    LaunchedEffect(key1 = itemId) {
        itemId?.let { id ->
            homePlaylistItemViewModel.initializePlaylistPager(id)
        }
    }

    when (val state = playlistItemsState) {
        is PaginatedState.Initial -> {
        }

        is PaginatedState.Loading -> {
            LoadingIndicator()
        }

        is PaginatedState.Success -> {
            EndlessLazyColumn(
                items = state.items,
                headerData = playlistInfo,
                itemKey = { item: NewPipeContentListData -> item.id },
                itemContent = { index, item: NewPipeContentListData ->
                    CommonVideoItem(item = item as NewPipeVideoData, currentIndex = index, onClick = {
                        mainViewModel.expandBottomSheet()
                        mediaViewModel.onMediaItemClick(
                            item = item,
                            playlistItems = state.items,
                            clickedIndex = index
                        )
                    },
                        dropDownMenuClick = {
                            mainViewModel.showAddToPlaylistDialog(item)
                        })
                },
                headerContent = { playlistData ->
                    PlaylistHeaderItem(playlistData = playlistData)
                },
                loading = state.isLoadingMore,
                loadMore = { homePlaylistItemViewModel.loadMorePlaylistItems() },
                hasMoreItems = state.hasMore
            )
        }

        is PaginatedState.Error -> {
            ErrorMessage(message = state.message)
        }
    }
    if (isShowingPlaylistDialog) {
        AddVideoToPlaylistDialog(
            playlists = myPlaylists,
            onDismiss = { mainViewModel.dismissPlaylistDialog() },
            onPlaylistSelected = { playlistId ->
                selectedVideo?.let {
                    mainViewModel.addVideoToPlaylist(it, playlistId)
                }
            }
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    // 에러 메시지 표시 구현
}