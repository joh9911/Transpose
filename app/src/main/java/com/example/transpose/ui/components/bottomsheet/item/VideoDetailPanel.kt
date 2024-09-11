package com.example.transpose.ui.components.bottomsheet.item

import android.os.Build
import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.common.PlayableItemUiState
import com.example.transpose.utils.Logger
import kotlinx.coroutines.launch

@Composable
fun VideoDetailPanel(
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier,
) {
    val currentVideoItemState by mediaViewModel.currentVideoItemState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LazyColumn(
        modifier = modifier,
        state = listState
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
                            onClick = { mediaViewModel.onMediaItemClick(item)
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        )
                    }
                }

            }
            PlayableItemUiState.Initial -> {


            }
        }


    }
}