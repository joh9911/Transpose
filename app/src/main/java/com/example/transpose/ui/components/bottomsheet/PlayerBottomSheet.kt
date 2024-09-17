package com.example.transpose.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.transpose.ui.common.PlayableItemUiState
import com.example.transpose.ui.components.bottomsheet.GraphicsLayerConstants.PEEK_HEIGHT
import com.example.transpose.ui.components.bottomsheet.item.PlayerLoadingIndicator
import com.example.transpose.ui.components.bottomsheet.item.PlayerThumbnailView
import com.example.transpose.ui.components.bottomsheet.item.VideoDetailPanel
import com.example.transpose.utils.Logger
import com.example.transpose.utils.ToastUtil
import com.example.transpose.utils.constants.AppColors


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

    val currentVideoItemState by mediaViewModel.currentVideoItemState.collectAsState()
    val bottomSheetDraggableArea by mainViewModel.bottomSheetDraggableArea.collectAsState()


    var playerViewHeight by remember { mutableStateOf(0) }

    var dragStartPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)
            .nestedScroll(object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ) = available
            })
//            .pointerInput(Unit) {
//                awaitPointerEventScope {
//                    while (true) {
//                        val event = awaitPointerEvent()
//                        when (event.type) {
//                            PointerEventType.Press -> {
//                                dragStartPosition = event.changes.first().position
//                                isDragging = true
//                                Logger.d("Press $dragStartPosition")
//                            }
//
//                            PointerEventType.Move -> {
//                                if (isDragging) {
//                                    val currentPosition = event.changes.first().position
//                                    val dragAmount = currentPosition - dragStartPosition
//                                    Logger.d("Move $currentPosition ${bottomSheetDraggableArea?.contains(currentPosition)}")
//
//                                    bottomSheetDraggableArea?.let {
//                                        mainViewModel.updateIsBottomSheetDraggable(
//                                            it.contains(
//                                                currentPosition
//                                            )
//                                        )
//                                    }
//                                }
//                            }
//
//                            PointerEventType.Release -> {
//                                if (isDragging) {
//                                    isDragging = false
//                                    Logger.d("Release $dragStartPosition")
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }

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
                .background(AppColors.BlueBackground)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        // 첫 번째 터치 다운 이벤트를 기다립니다
                        val down = awaitFirstDown(requireUnconsumed = false)
                        Logger.d("Touch Down detected at: ${down.position}")

                        // 터치 업 이벤트를 기다립니다
                        val up = waitForUpOrCancellation()
                        if (up != null) {
                            Logger.d("Touch Up detected at: ${up.position}")
                            mainViewModel.expandBottomSheet()
                        } else {
                            Logger.d("Touch cancelled")
                        }
                    }
                }
//                .onGloballyPositioned { coordinates ->
//                    mainViewModel.updateBottomSheetDraggableArea(coordinates.boundsInParent())
//                }

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
                .background(AppColors.LightGray)
        )

        // bottomTitleTextView

        Text(
            text = when (val state = currentVideoItemState) {
                is PlayableItemUiState.BasicInfoLoaded -> state.basicInfo.title
                is PlayableItemUiState.FullInfoLoaded -> state.fullInfo.title
                else -> ""
            },
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
            onClick = { ToastUtil.showNotImplemented(context = context) },
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
                    view.useController = when (bottomSheetState) {
                        SheetValue.Expanded -> true
                        else -> false
                    }

                },
                modifier = Modifier.fillMaxSize()
            )
            PlayerThumbnailView(
                mediaViewModel = mediaViewModel,
                modifier = Modifier.fillMaxSize()

            )
            PlayerLoadingIndicator(
                mediaViewModel = mediaViewModel,
                modifier = Modifier.align(Alignment.Center)
            )

        }

        VideoDetailPanel(
            mediaViewModel = mediaViewModel,
            mainViewModel = mainViewModel,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .constrainAs(playlistBottomSheet) {
                    top.linkTo(playerContainer.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .graphicsLayer(
                    translationY = -playerViewHeight * (1 - calculateScaleFactorY(normalizedOffset))
                )
                .changeMainBackgroundAlpha(normalizedOffset)

        )


//        PlaylistBottomSheet(
//            mainViewModel = mainViewModel,
//            mediaViewModel = mediaViewModel,
//            modifier = Modifier
//                .constrainAs(playlistBottomSheet) {
//                    top.linkTo(playerContainer.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                    bottom.linkTo(parent.bottom)
//                    height = Dimension.fillToConstraints
//                }
//                .graphicsLayer(
//                    translationY = -playerViewHeight * (1 - calculateScaleFactorY(normalizedOffset))
//                )
//                .changeMainBackgroundAlpha(normalizedOffset)
//        ) { paddingValues ->
//
//            VideoDetailPanel(
//                mediaViewModel = mediaViewModel,
//                mainViewModel = mainViewModel,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White)
//                    .padding(paddingValues)
//            )
//        }
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

