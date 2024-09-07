package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.common.PlayableItemUiState

@Composable
fun PlayerThumbnailView(
    mediaViewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val currentVideoItemState by mediaViewModel.currentVideoItemState.collectAsState()

    when (val state = currentVideoItemState) {

        PlayableItemUiState.Initial -> {}
        is PlayableItemUiState.BasicInfoLoaded -> {
            val data = state.basicInfo
            Box(modifier = modifier) {

                AsyncImage(
                    model = data.thumbnailUrl,
                    contentDescription = "Video Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

            }
        }

        is PlayableItemUiState.Error -> {
        }

        is PlayableItemUiState.FullInfoLoaded -> {}
    }

}