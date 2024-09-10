package com.example.transpose.ui.components.bottomsheet.item

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.common.PlayableItemUiState
import com.example.transpose.utils.Logger

@Composable
fun VideoDetailPanel(
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier,
) {
    val currentVideoItemState by mediaViewModel.currentVideoItemState.collectAsState()
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)


    LazyColumn(
        modifier = modifier,
    ) {
        item {
            VideoInfoHeader(mediaViewModel = mediaViewModel, mainViewModel = mainViewModel)
        }

        when (val state = currentVideoItemState) {

            is PlayableItemUiState.BasicInfoLoaded -> {
                items(5){
                    RelatedVideoShimmerItem()
                }
            }
            is PlayableItemUiState.Error -> {
                val data = state.message
                item{
                    Text(data ?: "")

                }
            }
            is PlayableItemUiState.FullInfoLoaded -> {
                val items = state.fullInfo.relatedItems
                items?.let { videoList ->
                    items(videoList.size) { index ->
                        val item = videoList[index]
                        RelatedVideoItem(
                            infoItem = item,
                            mediaViewModel = mediaViewModel
                        )
                    }
                }

            }
            PlayableItemUiState.Initial -> {


            }
        }


    }
}