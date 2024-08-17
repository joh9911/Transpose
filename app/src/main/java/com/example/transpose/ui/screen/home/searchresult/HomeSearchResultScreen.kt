package com.example.transpose.ui.screen.home.searchresult

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transpose.NavigationViewModel
import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.ui.common.UiState
import com.example.transpose.ui.components.LoadingIndicator
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn
import com.example.transpose.ui.screen.home.HomeViewModel
import com.example.transpose.utils.Logger

@Composable
fun HomeSearchResultScreen(
    viewModel: HomeViewModel,
    query: String?,
    navigationViewModel: NavigationViewModel
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val uiState by viewModel.searchUiState.collectAsState()
    val isMoreItemsLoading by viewModel.isMoreItemsLoading.collectAsState()
    val hasMoreItems by viewModel.hasMoreSearchItems.collectAsState()

    val listState = rememberLazyListState()

    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(key1 = true) {
        query?.let {
            viewModel.initializeSearchPager(it)
        }

    }
    when (uiState) {
        is UiState.Initial -> {
            // 초기 상태 UI (예: 검색 안내 메시지)
        }

        is UiState.Loading -> {
            LoadingIndicator()
        }

        is UiState.Success -> {
            EndlessLazyColumn(
                modifier = Modifier.fillMaxSize(),
                items = searchResults,
                headerData = null,
                itemKey = { item: NewPipeContentListData -> item.id },
                itemContent = { item: NewPipeContentListData ->
                    CommonVideoItem(item = item, onClick = {})
                },

                loading = isMoreItemsLoading,
                loadMore = {viewModel.loadMoreSearchResults()},
                hasMoreItems = hasMoreItems
            )
        }

        is UiState.Error -> {
            ErrorMessage(message = (uiState as UiState.Error).message)
        }
    }

}




@Composable
fun ErrorMessage(message: String) {
    // 에러 메시지 표시 구현
}
