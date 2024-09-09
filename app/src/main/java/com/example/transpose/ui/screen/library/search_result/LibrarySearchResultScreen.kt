package com.example.transpose.ui.screen.library.search_result

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.common.PaginatedState
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.items.LoadingIndicator
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySearchResultScreen(
    librarySearchResultViewModel: LibrarySearchResultViewModel,
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    query: String?
) {
    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val searchResultsState by librarySearchResultViewModel.searchResultsState.collectAsState()

    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }

    LaunchedEffect(key1 = query) {
        query?.let {
            librarySearchResultViewModel.initializeSearchPager(it)
        }
    }

    when (val state = searchResultsState) {
        is PaginatedState.Initial -> {
            // 초기 상태 UI (예: 검색 안내 메시지)
        }
        is PaginatedState.Loading -> {
            LoadingIndicator()
        }
        is PaginatedState.Success -> {
            EndlessLazyColumn(
                modifier = Modifier.fillMaxSize(),
                items = state.items,
                headerData = null,
                itemKey = { item: NewPipeContentListData -> item.id },
                itemContent = {index, item: NewPipeContentListData ->
                    CommonVideoItem(
                        item = item,
                        currentIndex = index,
                        onClick = {
                            mediaViewModel.onMediaItemClick(item as NewPipeVideoData)
                            mainViewModel.expandBottomSheet()
                        }
                    )
                },
                loading = state.isLoadingMore,
                loadMore = { librarySearchResultViewModel.loadMoreSearchResults() },
                hasMoreItems = state.hasMore
            )
        }
        is PaginatedState.Error -> {
            ErrorMessage(message = state.message)
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    // 에러 메시지 표시 구현
}