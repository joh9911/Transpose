package com.example.transpose.ui.screen.home.search_result

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import com.example.transpose.data.model.newpipe.NewPipeChannelData
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.model.newpipe.NewPipePlaylistData
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.ui.common.PaginatedState
import com.example.transpose.ui.components.dialog.AddVideoToPlaylistDialog
import com.example.transpose.ui.components.items.ChannelItem
import com.example.transpose.ui.components.items.CommonVideoItem
import com.example.transpose.ui.components.items.LoadingIndicator
import com.example.transpose.ui.components.items.PlaylistItem
import com.example.transpose.ui.components.scrollbar.EndlessLazyColumn
import org.schabi.newpipe.extractor.InfoItem

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchResultScreen(
    homeSearchResultViewModel: HomeSearchResultViewModel,
    navigationViewModel: NavigationViewModel,
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    query: String?,
) {
    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val searchResultsState by homeSearchResultViewModel.searchResultsState.collectAsState()
    val isShowingPlaylistDialog  by mainViewModel.isShowAddVideoToPlaylistDialog.collectAsState()
    val myPlaylists by mainViewModel.myPlaylists.collectAsState()
    val selectedVideo by mainViewModel.selectedVideo.collectAsState()

    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }

    LaunchedEffect(key1 = query) {
        query?.let {
            homeSearchResultViewModel.initializeSearchPager(it)
        }
    }

    when (val state = searchResultsState) {
        is PaginatedState.Initial -> {
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
                    when (item.infoType) {
                        InfoItem.InfoType.PLAYLIST -> {
                            val newPipePlaylistData = item as NewPipePlaylistData
                            PlaylistItem(playlist = (newPipePlaylistData), onClick = {})
                        }

                        InfoItem.InfoType.STREAM -> {
                            val newPipeVideoData = item as NewPipeVideoData
                            CommonVideoItem(
                                item = newPipeVideoData,
                                currentIndex = index,
                                onClick = {
                                    mediaViewModel.onMediaItemClick(newPipeVideoData)
                                    mainViewModel.expandBottomSheet()
                                },
                                dropDownMenuClick = {mainViewModel.showAddToPlaylistDialog(newPipeVideoData)}
                            )

                        }

                        InfoItem.InfoType.CHANNEL -> {
                            val newPipeChannelData = item as NewPipeChannelData
                            ChannelItem(
                                channel = newPipeChannelData,
                                onClick = {
                                }
                            )
                        }

                        InfoItem.InfoType.COMMENT -> {}
                    }

                },
                loading = state.isLoadingMore,
                loadMore = { homeSearchResultViewModel.loadMoreSearchResults() },
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