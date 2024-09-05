package com.example.transpose.ui.components.bottomsheet

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.ui.components.bottomsheet.GraphicsLayerConstants.PEEK_HEIGHT
import com.example.transpose.ui.components.bottomsheet.item.ChannelLayout
import com.example.transpose.ui.components.bottomsheet.item.PitchControlItem
import com.example.transpose.ui.components.bottomsheet.item.PlayerLoadingIndicator
import com.example.transpose.ui.components.bottomsheet.item.PlayerThumbnailView
import com.example.transpose.ui.components.bottomsheet.item.RelatedVideoItem
import com.example.transpose.ui.components.bottomsheet.item.TempoControlItem
import com.example.transpose.ui.components.bottomsheet.item.VideoInfoItem
import com.example.transpose.utils.Logger
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map


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
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
) {

    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val mediaController by mediaViewModel.mediaController.collectAsState()

    val normalizedOffset by mainViewModel.normalizedOffset.collectAsState()

    val isPlaying by mediaViewModel.isPlaying.collectAsState()

    val currentVideoItem by mediaViewModel.currentVideoItem.collectAsState()

    val draggableAreaBounds by mainViewModel.bottomSheetDraggableArea.collectAsState()


    val listState = rememberLazyListState()

    val isFocused by listState.interactionSource.interactions
        .filterIsInstance<DragInteraction>()
        .map { dragInteraction ->
            dragInteraction is DragInteraction.Start
        }
        .collectAsState(false)

    var playerViewHeight by remember { mutableStateOf(0) }

    val playerViewKey = remember { mutableStateOf(0) }

    LaunchedEffect(bottomSheetState) {
        playerViewKey.value += 1
    }


    LaunchedEffect(isFocused) {
        Logger.d("LaunchedEffect(isFocused) ")
        mainViewModel.updateIsBottomSheetDraggable(false)
    }


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)
    ) {

        val (playerContainer, mainContainerLayout, playerView, playerThumbnailView, tempPlayerView, bottomPlayerCloseButton, bottomPlayerPauseButton, bottomTitleTextView, contentLazyColumn, playlistShowButton, playlistBottomSheet) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(GraphicsLayerConstants.DEFAULT_HEIGHT)
                .constrainAs(mainContainerLayout) {
                    top.linkTo(parent.top)
                }
                .graphicsLayer(
                    scaleY = calculateScaleFactorY(normalizedOffset),
                    transformOrigin = TransformOrigin(0.5f, 0f)  // pivotY = 0f에 해당
                )
                .background(Color.Blue)
                .onGloballyPositioned { coordinates ->
                    mainViewModel.updateBottomSheetDraggableArea(coordinates.boundsInWindow())
                }
        )

        val centerGuideline = createGuidelineFromTop(PEEK_HEIGHT / 2)

        Box(
            modifier = Modifier
                .constrainAs(tempPlayerView) {
                    start.linkTo(parent.start)
                    top.linkTo(centerGuideline)
                    bottom.linkTo(centerGuideline)
                    width = Dimension.percent(0.3f)
                }
                .height(PEEK_HEIGHT)
                .background(Color.LightGray)
        )

        // bottomTitleTextView
        Text(
            text = currentVideoItem?.title ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier
                .constrainAs(bottomTitleTextView) {
                    start.linkTo(tempPlayerView.end, margin = 8.dp)
                    end.linkTo(bottomPlayerPauseButton.start, margin = 12.dp)
                    top.linkTo(centerGuideline)
                    bottom.linkTo(centerGuideline)
                    width = Dimension.fillToConstraints
                }
                .bottomSheetAlpha(normalizedOffset)
        )

        // bottomPlayerCloseButton
        IconButton(
            onClick = { /* Close logic */ },
            modifier = Modifier
                .constrainAs(bottomPlayerCloseButton) {
                    end.linkTo(parent.end)
                    top.linkTo(centerGuideline)
                    bottom.linkTo(centerGuideline)
                }
                .bottomSheetAlpha(normalizedOffset)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        // bottomPlayerPauseButton
        IconButton(
            onClick = { mediaViewModel.playPause() },
            modifier = Modifier
                .constrainAs(bottomPlayerPauseButton) {
                    end.linkTo(bottomPlayerCloseButton.start, margin = 5.dp)
                    top.linkTo(centerGuideline)
                    bottom.linkTo(centerGuideline)

                }
                .bottomSheetAlpha(normalizedOffset)
        ) {
            Icon(
                painterResource(id = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24),
                contentDescription = "Play/Pause",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(playerContainer) {
                    top.linkTo(mainContainerLayout.top)
                    start.linkTo(mainContainerLayout.start)
                    end.linkTo(mainContainerLayout.end)
                    bottom.linkTo(mainContainerLayout.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .onGloballyPositioned { coordinates ->
                    playerViewHeight = coordinates.size.height
                }

                .graphicsLayer(
                    scaleX = when {
                        normalizedOffset < 0f -> GraphicsLayerConstants.MIN_SCALE
                        normalizedOffset < 0.2f -> calculateDefaultScaleX(normalizedOffset)
                        else -> 1f
                    },
                    scaleY = calculateScaleFactorY(normalizedOffset),
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL  // 또는 RESIZE_MODE_FIT

                    }
                },
                update = { view ->
                    mediaController?.let { controller ->
                        view.player = controller
                    } ?: run {
                        view.player = null
                    }
                    Logger.d("AndroidView $bottomSheetState")

                    view.useController = when (bottomSheetState) {
                        SheetValue.Expanded -> true
                        else -> false
                    }

                },
                modifier = Modifier.fillMaxSize()
            )
            PlayerLoadingIndicator(
                mediaViewModel = mediaViewModel,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // playerThumbnailView
        PlayerThumbnailView(
            mediaViewModel = mediaViewModel,
            modifier = Modifier
                .constrainAs(playerThumbnailView) {
                    top.linkTo(playerContainer.top)
                    start.linkTo(playerContainer.start)
                    end.linkTo(playerContainer.end)
                    bottom.linkTo(playerContainer.bottom)
                }
                .graphicsLayer(
                    scaleX = when {
                        normalizedOffset < 0f -> GraphicsLayerConstants.MIN_SCALE
                        normalizedOffset < 0.2f -> calculateDefaultScaleX(normalizedOffset)
                        else -> 1f
                    },
                    scaleY = calculateScaleFactorY(normalizedOffset),
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        )

        PlaylistBottomSheet(
            mainViewModel = mainViewModel,
            mediaViewModel = mediaViewModel,
            modifier = Modifier
                .constrainAs(playlistBottomSheet) {
                    top.linkTo(playerContainer.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }.graphicsLayer(
                    translationY = -playerViewHeight * (1 - calculateScaleFactorY(normalizedOffset))
                )
                .changeMainBackgroundAlpha(normalizedOffset)
        ){ paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

            ) {
                item {
                    VideoInfoItem(currentVideoItem)
                }
                item {
                    ChannelLayout(
                        currentVideoItem = currentVideoItem,
                        mediaViewModel = mediaViewModel,
                        mainViewModel = mainViewModel
                    )
                }
                item {
                    PitchControlItem(mediaViewModel)
                }
                item {
                    TempoControlItem(mediaViewModel)
                }

                items(20) { index ->
                    RelatedVideoItem(index)
                }
            }
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
    return this.alpha((normalizedOffset * normalizedOffset * normalizedOffset).coerceAtLeast(0f))


}

private fun calculateDefaultScaleX(normalizedOffset: Float): Float {
    return lerp(start = 0.3f, stop = 1f, fraction = normalizedOffset / 0.2f)
}

private fun calculateScaleFactorY(normalizedOffset: Float): Float {
    val minScale =
        GraphicsLayerConstants.PEEK_HEIGHT.value / GraphicsLayerConstants.DEFAULT_HEIGHT.value
    return when {
        normalizedOffset <= GraphicsLayerConstants.FULLY_EXPANDED -> minScale
        else -> lerp(start = minScale, stop = 1f, fraction = normalizedOffset)
    }
}

