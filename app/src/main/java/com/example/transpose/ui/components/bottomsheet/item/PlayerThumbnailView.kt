package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.transpose.MediaViewModel

@Composable
fun PlayerThumbnailView(
    mediaViewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val showThumbnail by mediaViewModel.isShowingThumbnail.collectAsState()
    val currentItem by mediaViewModel.currentVideoItem.collectAsState()

    Box(modifier = modifier) {
        if (showThumbnail) {
            AsyncImage(
                model = currentItem?.thumbnailUrl,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}