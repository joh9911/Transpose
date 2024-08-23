package com.example.transpose.ui.screen.home.searchresult

import android.provider.MediaStore.Audio.Media
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.NavigationViewModel
import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipeVideoData
import com.example.transpose.ui.common.UiState
import com.example.transpose.ui.components.items.LoadingIndicator
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn
import com.example.transpose.utils.Logger

@Composable
fun HomeSearchResultScreen(
    homeSearchResultViewModel: HomeSearchResultViewModel,
    navigationViewModel: NavigationViewModel,
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    query: String?,
) {

    val searchResults by homeSearchResultViewModel.searchResults.collectAsState()
    val uiState by homeSearchResultViewModel.searchUiState.collectAsState()
    val isMoreItemsLoading by homeSearchResultViewModel.isMoreItemsLoading.collectAsState()
    val hasMoreItems by homeSearchResultViewModel.hasMoreSearchItems.collectAsState()

    val listState = rememberLazyListState()


    LaunchedEffect(key1 = true) {
        query?.let {
            homeSearchResultViewModel.initializeSearchPager(it)
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
                    CommonVideoItem(
                        item = item,
                        onClick = { mediaViewModel.setMediaItem(item as NewPipeVideoData)
                            mainViewModel.showBottomSheet()

                        })
                },

                loading = isMoreItemsLoading,
                loadMore = { homeSearchResultViewModel.loadMoreSearchResults() },
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
