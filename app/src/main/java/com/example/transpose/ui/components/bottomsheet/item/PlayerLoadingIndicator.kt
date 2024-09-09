package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.common.PlayableItemUiState

@Composable
fun PlayerLoadingIndicator(
    mediaViewModel: MediaViewModel, modifier: Modifier = Modifier
) {
    val currentVideoState by mediaViewModel.currentVideoItemState.collectAsState()
    val isPlaying by mediaViewModel.isPlaying.collectAsState()

    when (val state = currentVideoState) {
        is PlayableItemUiState.BasicInfoLoaded -> {

                CircularProgressIndicator(
                    modifier = modifier
                )
        }

        is PlayableItemUiState.Error -> {}
        is PlayableItemUiState.FullInfoLoaded -> {}
        PlayableItemUiState.Initial -> {

                CircularProgressIndicator(
                    modifier = modifier
                )
        }
    }

}