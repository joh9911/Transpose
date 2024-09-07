package com.example.transpose.ui.screen.home.playlist_item

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MainViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.ui.common.PaginatedState
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.items.LoadingIndicator
import com.example.transpose.ui.components.items.PlaylistHeaderItem
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePlaylistItemScreen(
    mainViewModel: MainViewModel,
    homePlaylistItemViewModel: HomePlaylistItemViewModel,
    navigationViewModel: NavigationViewModel,
    itemId: String?,
) {
    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val playlistInfo by homePlaylistItemViewModel.playlistInfo.collectAsState()
    val playlistItemsState by homePlaylistItemViewModel.playlistItemsState.collectAsState()

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
            // 초기 상태 UI (예: 안내 메시지)
        }
        is PaginatedState.Loading -> {
            LoadingIndicator()
        }
        is PaginatedState.Success -> {
            EndlessLazyColumn(
                items = state.items,
                headerData = playlistInfo,
                itemKey = { item: NewPipeContentListData -> item.id },
                itemContent = { item: NewPipeContentListData ->
                    CommonVideoItem(item = item, onClick = {
                        // 비디오 아이템 클릭 처리
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
            // 에러 메시지 표시
            ErrorMessage(message = state.message)
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    // 에러 메시지 표시 구현
}