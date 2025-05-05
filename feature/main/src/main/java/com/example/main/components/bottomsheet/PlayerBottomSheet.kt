package com.example.main.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.main.MainViewModel
import com.example.main.R
import com.example.main.components.bottomsheet.GraphicsLayerConstants.PEEK_HEIGHT
import com.example.main.components.bottomsheet.item.PlayerLoadingIndicator
import com.example.main.components.bottomsheet.item.PlayerThumbnailView
import com.example.main.components.bottomsheet.item.PlaylistFloatingButton
import com.example.main.components.bottomsheet.item.VideoDetailPanel
import com.example.util.constants.AppColors
import kotlinx.coroutines.launch
import kotlin.math.pow


object GraphicsLayerConstants {
    const val FULLY_EXPANDED = 0f
    const val SCALE_THRESHOLD = 0.2f
    const val MIN_SCALE = 0.3f

    val PEEK_HEIGHT = 56.dp
    val DEFAULT_HEIGHT = 250.dp
}


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheet(
    mainViewModel: MainViewModel,
    bottomSheetState: SheetState,
    normalizedOffset: Float,
    onNavigateToChannelScreen: (String) -> Unit
) {
    val mediaController by mainViewModel.mediaControllerFlow.collectAsState()
    val currentVideoItem by mainViewModel.currentVideoData.collectAsState()
    val currentVideoDetailData by mainViewModel.currentVideoDetailData.collectAsState()
    val isPlaying by mainViewModel.isPlaying.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val currentPlaylistItems by mainViewModel.currentPlaylistItems.collectAsState()
    var showPlaylistModal by remember { mutableStateOf(false) }


    val scaleY = remember(normalizedOffset) {
        calculateScaleFactorY(normalizedOffset)
    }

    val scaleX = remember(normalizedOffset) {
        when {
            normalizedOffset < 0f -> GraphicsLayerConstants.MIN_SCALE
            normalizedOffset < 0.2f -> calculateDefaultScaleX(normalizedOffset)
            else -> 1f
        }
    }

    val bottomSheetAlpha = remember(normalizedOffset) {
        if (normalizedOffset < 0) 1f else {
            when {
                normalizedOffset < 0.2f -> {
                    val alphaValue = (0.2 - normalizedOffset) / 0.2
                    alphaValue
                }

                else -> 0f
            }
        }
    }

    val mainBackgroundAlpha = remember(normalizedOffset) {
        if (normalizedOffset < 0) 1f else {
            normalizedOffset.pow(3).coerceAtLeast(0f)
        }
    }

    var playerViewHeight by remember { mutableStateOf(0) }

    PlaylistModalBottomSheet(
        showPlaylist = showPlaylistModal,
        onDismiss = { showPlaylistModal = false },
        mainViewModel = mainViewModel,
        playerViewHeight = playerViewHeight
    )

    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(GraphicsLayerConstants.DEFAULT_HEIGHT)
                .graphicsLayer(
                    scaleY = scaleY,
                    transformOrigin = TransformOrigin(0.5f, 0f)  // pivotY = 0f에 해당
                )
                .background(AppColors.BlueBackground)
                .clickable {
                    coroutineScope.launch {
                        bottomSheetState.expand()
                    }
                }
        )
        Row(
            modifier = Modifier
                .height(PEEK_HEIGHT),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f)
                    .background(AppColors.BlueBackground)
            )

            Text(
                text = currentVideoItem?.title ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 12.dp)
                    .bottomSheetAlpha(normalizedOffset)
            )

            IconButton(
                onClick = { mainViewModel.playPause() },
                modifier = Modifier
                    .padding(end = 5.dp)
                    .bottomSheetAlpha(normalizedOffset)
            ) {
                Icon(
                    painterResource(id = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24),
                    contentDescription = "Play/Pause",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    mainViewModel.stopPlayback()
                    coroutineScope.launch { bottomSheetState.hide() }
                },
                modifier = Modifier
                    .bottomSheetAlpha(normalizedOffset)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GraphicsLayerConstants.DEFAULT_HEIGHT)
                    .onGloballyPositioned { coordinates ->
                        playerViewHeight = coordinates.size.height
                    }
                    .graphicsLayer(
                        scaleX = scaleX,
                        scaleY = scaleY,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            keepScreenOn = true
                        }
                    },
                    update = { view ->
                        mediaController?.let { controller ->
                            view.player = controller
                        } ?: run {
                            view.player = null
                        }
                        view.useController = when (bottomSheetState.currentValue) {
                            SheetValue.Expanded -> true
                            else -> false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                PlayerThumbnailView(
                    currentVideoItem,
                    currentVideoDetailData,
                    isPlaying,
                    modifier = Modifier.fillMaxSize()
                )
                PlayerLoadingIndicator(
                    videoDetail = currentVideoDetailData,
                    isPlaying = isPlaying,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            VideoDetailPanel(
                currentVideoData = currentVideoItem,
                currentVideoDetail = currentVideoDetailData,
                mainViewModel = mainViewModel,
                onNavigateToChannelScreen = onNavigateToChannelScreen,
                bottomSheetState = bottomSheetState,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        translationY = -playerViewHeight * (1 - scaleY)
                    )
                    .changeMainBackgroundAlpha(normalizedOffset)
            )
        }
        if (currentPlaylistItems.isNotEmpty() && bottomSheetState.currentValue == SheetValue.Expanded) {
            PlaylistFloatingButton(
                playlistSize = currentPlaylistItems.size,
                onClick = { showPlaylistModal = true },
                normalizedOffset = normalizedOffset,
                mainViewModel = mainViewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}


private fun Modifier.bottomSheetAlpha(normalizedOffset: Float): Modifier {
    if (normalizedOffset < 0) return this.alpha(1f)

    return this.alpha(
        alpha = when {
            normalizedOffset < 0.2f -> {
                val alphaValue = (0.2 - normalizedOffset) / 0.2
                alphaValue.toFloat()
            }

            else -> 0f
        }
    )
}

private fun Modifier.changeMainBackgroundAlpha(normalizedOffset: Float): Modifier {
    if (normalizedOffset < 0) return alpha(1f)
    return this.alpha((normalizedOffset.pow(3)).coerceAtLeast(0f))


}

private fun calculateDefaultScaleX(normalizedOffset: Float): Float {
    val t = (normalizedOffset / 0.2f).coerceIn(0f, 1f)
    val easedT = t * t * t
    return GraphicsLayerConstants.MIN_SCALE + (1f - GraphicsLayerConstants.MIN_SCALE) * easedT
}

private fun calculateScaleFactorY(normalizedOffset: Float): Float {
    val minScale =
        PEEK_HEIGHT.value / GraphicsLayerConstants.DEFAULT_HEIGHT.value
    return when {
        normalizedOffset <= GraphicsLayerConstants.FULLY_EXPANDED -> minScale
        else -> lerp(start = minScale, stop = 1f, fraction = normalizedOffset)
    }
}

