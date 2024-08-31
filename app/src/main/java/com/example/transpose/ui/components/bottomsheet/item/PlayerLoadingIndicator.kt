package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.transpose.MediaViewModel

@Composable
fun PlayerLoadingIndicator(
    mediaViewModel: MediaViewModel, modifier: Modifier = Modifier
) {
    val isShowingLoadingIndicator by mediaViewModel.isShowingLoadingIndicator.collectAsState()
    if (isShowingLoadingIndicator){
        CircularProgressIndicator(
            modifier = modifier
        )
    }

}