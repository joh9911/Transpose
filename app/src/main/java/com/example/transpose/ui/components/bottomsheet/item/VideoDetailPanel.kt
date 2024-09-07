package com.example.transpose.ui.components.bottomsheet.item

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.media.model.PlayableItemUiState
import com.example.transpose.ui.common.UiState
import com.example.transpose.ui.components.items.LoadingIndicator

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoDetailPanel(
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier,
) {
    val currentVideoItemState by mediaViewModel.currentVideoItemState.collectAsState()

    LazyColumn(
        modifier = modifier
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