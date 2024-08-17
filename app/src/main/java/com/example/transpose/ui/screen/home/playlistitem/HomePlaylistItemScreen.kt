package com.example.transpose.ui.screen.home.playlistitem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.transpose.NavigationViewModel
import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.ui.common.UiState
import com.example.transpose.ui.components.LoadingIndicator
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.items.PlaylistHeaderItem
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn
import com.example.transpose.ui.screen.home.HomeViewModel
import com.example.transpose.utils.Logger

@Composable
fun HomePlaylistItemScreen(
    homeViewModel: HomeViewModel,
    itemId: String?,
    navigationViewModel: NavigationViewModel
) {
    val playlistInfo by homeViewModel.playlistInfo.collectAsState()
    val playlistItems by homeViewModel.playlistItems.collectAsState()
    val playlistItemUiState by homeViewModel.playlistItemUiState.collectAsState()
    val hasMoreItems by homeViewModel.hasMorePlaylistItems.collectAsState()
    val isMorePlaylistItemsLoading by homeViewModel.isMorePlaylistItemsLoading.collectAsState()

    LaunchedEffect(key1 = true) {
        itemId?.let { id ->
            homeViewModel.initializePlaylistPager(id)
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
                loadMore = { homeViewModel.loadMorePlaylistItems() },
                hasMoreItems = hasMoreItems
            )
        }
    }

}
