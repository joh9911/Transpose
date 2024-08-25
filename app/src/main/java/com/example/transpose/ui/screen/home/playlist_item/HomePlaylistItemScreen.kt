package com.example.transpose.ui.screen.home.playlist_item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.ui.common.UiState
import com.example.transpose.ui.components.items.LoadingIndicator
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.items.PlaylistHeaderItem
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn

@Composable
fun HomePlaylistItemScreen(
    homePlaylistItemViewModel: HomePlaylistItemViewModel,
    navigationViewModel: NavigationViewModel,
    itemId: String?,
) {
    val playlistInfo by homePlaylistItemViewModel.playlistInfo.collectAsState()
    val playlistItems by homePlaylistItemViewModel.playlistItems.collectAsState()
    val playlistItemUiState by homePlaylistItemViewModel.playlistItemUiState.collectAsState()
    val hasMoreItems by homePlaylistItemViewModel.hasMorePlaylistItems.collectAsState()
    val isMorePlaylistItemsLoading by homePlaylistItemViewModel.isMorePlaylistItemsLoading.collectAsState()

    LaunchedEffect(key1 = true) {
        itemId?.let { id ->
            homePlaylistItemViewModel.initializePlaylistPager(id)
        }
    }

    when(playlistItemUiState){
        is UiState.Initial -> {

        }

        is UiState.Error -> {

        }
        UiState.Loading -> {
            LoadingIndicator()
        }
        UiState.Success -> {
            EndlessLazyColumn(
                items = playlistItems,
                headerData = playlistInfo,
                itemKey = { item: NewPipeContentListData -> item.id },
                itemContent = { item: NewPipeContentListData ->
                    CommonVideoItem(item = item, onClick = {})
                },
                headerContent = { PlaylistHeaderItem(playlistData = playlistInfo)},
                loading = isMorePlaylistItemsLoading,
                loadMore = { homePlaylistItemViewModel.loadMorePlaylistItems() },
                hasMoreItems = hasMoreItems
            )
        }
    }

}
