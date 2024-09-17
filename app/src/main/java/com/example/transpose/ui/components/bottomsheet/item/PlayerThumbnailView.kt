package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.transpose.MediaViewModel
import com.example.transpose.media.model.MediaItemType
import com.example.transpose.ui.common.PlayableItemUiState
import com.example.transpose.utils.Logger
import com.example.transpose.utils.constants.AppColors

@Composable
fun PlayerThumbnailView(
    mediaViewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val currentVideoItemState by mediaViewModel.currentVideoItemState.collectAsState()
    val isPlaying by mediaViewModel.isPlaying.collectAsState()

    when (val state = currentVideoItemState) {

        PlayableItemUiState.Initial -> {}
        is PlayableItemUiState.BasicInfoLoaded -> {

            val data = state.basicInfo
            if (data.type == MediaItemType.YOUTUBE){
                Box(modifier = modifier.background(AppColors.LightGray)) {
                    AsyncImage(
                        model = data.thumbnailUrl,
                        contentDescription = "Video Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

        }

        is PlayableItemUiState.Error -> {

        }

        is PlayableItemUiState.FullInfoLoaded -> {}
    }

}