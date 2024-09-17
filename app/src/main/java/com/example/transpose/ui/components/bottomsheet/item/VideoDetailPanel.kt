package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.media.model.MediaItemType
import com.example.transpose.ui.common.PlayableItemUiState
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.InfoItem

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
                if (state.basicInfo.type == MediaItemType.YOUTUBE)
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
                        if (item.infoType == InfoItem.InfoType.STREAM){
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

            }
            PlayableItemUiState.Initial -> {


            }
        }


    }
}