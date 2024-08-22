package com.example.transpose.ui.components.bottomsheet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.ui.components.bottomsheet.item.PitchControlItem
import com.example.transpose.ui.components.bottomsheet.item.TempoControlItem
import com.example.transpose.ui.components.bottomsheet.item.VideoInfoItem
import com.example.transpose.utils.Logger
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.internal.notify

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheet(
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
) {

    val mediaController by mediaViewModel.mediaController.collectAsState()


    val requiredOffset by mainViewModel.requiredOffset.collectAsState()

    val searchWidgetState by mainViewModel.searchWidgetState.collectAsState()
    val bottomSheetDraggableArea by mainViewModel.bottomSheetDraggableArea.collectAsState()
    val isBottomSheetDraggable by mainViewModel.isBottomSheetDraggable.collectAsState()

    val defaultHeight = 250.dp

    val listState = rememberLazyListState()
    val isFocused by listState.interactionSource.interactions
        .filterIsInstance<DragInteraction>()
        .map { dragInteraction ->
            dragInteraction is DragInteraction.Start
        }
        .collectAsState(false)

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(isFocused) {
        mainViewModel.updateIsBottomSheetDraggable(false)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)


    ) {

        // Offset을 0 ~ 1로 변환 (1945이 최대, 0이 최소)
        val normalizedOffset = 1 - (requiredOffset / 1949f)

        // ScaleFactor 계산
        val peekHeight = 56.dp


        val scaleFactorX = 1 - (0.2 - normalizedOffset) / 0.2 * 0.7
        val alphaValue = (0.2 - normalizedOffset) / 0.2

        val scaleFactor = remember(normalizedOffset) {
            when {
                normalizedOffset <= 0f -> peekHeight / defaultHeight  // normalizedOffset이 0 이하일 때는 peekHeight 유지
                else -> lerp(
                    start = peekHeight / defaultHeight,
                    stop = 1f,
                    fraction = normalizedOffset
                )
            }
        }


        val (playerContainer, mainContainerLayout, playerView, playerThumbnailView, tempPlayerView, bottomPlayerCloseButton, bottomPlayerPauseButton, bottomTitleTextView, contentLazyColumn) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(defaultHeight)
                .constrainAs(mainContainerLayout) {
                    top.linkTo(parent.top)
                }
                .graphicsLayer(
                    scaleY = scaleFactor,
                    transformOrigin = TransformOrigin(0.5f, 0f)  // pivotY = 0f에 해당
                )
                .background(Color.Blue)
                .onGloballyPositioned { coordinates ->
                    mainViewModel.updateBottomSheetDraggableArea(coordinates.boundsInWindow())
                }


        )

        // playerThumbnailView
        Box(
            modifier = Modifier
                .constrainAs(playerThumbnailView) {
                    top.linkTo(playerContainer.top)
                    start.linkTo(playerContainer.start)
                    end.linkTo(playerContainer.end)
                    bottom.linkTo(playerContainer.bottom)
                }
                .graphicsLayer(
                    scaleX = when {
                        normalizedOffset < 0f -> (1 - 0.2 / 0.2 * 0.7).toFloat()
                        normalizedOffset < 0.2f -> scaleFactorX.toFloat()
                        else -> 1f
                    },
                    scaleY = scaleFactor,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        )


        val centerGuideline = createGuidelineFromTop(peekHeight / 2)

        Box(
            modifier = Modifier
                .constrainAs(tempPlayerView) {
                    start.linkTo(parent.start)
                    top.linkTo(centerGuideline)
                    bottom.linkTo(centerGuideline)
                    width = Dimension.percent(0.3f)
                }
                .height(peekHeight)
                .background(Color.LightGray)
        )

        // bottomTitleTextView
        Text(
            text = "Video Title",
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
            onClick = { /* Play/Pause logic */ },
            modifier = Modifier
                .constrainAs(bottomPlayerPauseButton) {
                    end.linkTo(bottomPlayerCloseButton.start, margin = 5.dp)
                    top.linkTo(centerGuideline)
                    bottom.linkTo(centerGuideline)

                }
                .bottomSheetAlpha(normalizedOffset)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
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
                .graphicsLayer(
                    scaleX = when {
                        normalizedOffset < 0f -> (1 - 0.2 / 0.2 * 0.7).toFloat()
                        normalizedOffset < 0.2f -> scaleFactorX.toFloat()
                        else -> 1f
                    },
                    scaleY = scaleFactor,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        ) {
            AndroidView(
                factory = { ctx -> PlayerView(ctx).apply {} },
                update = { view ->
                    mediaController?.let { controller ->
                        view.player = controller
                    } ?: run {
                        view.player = null
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)

            )
        }


        LazyColumn(
            modifier = Modifier
                .constrainAs(contentLazyColumn) {
                    top.linkTo(playerContainer.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .nestedScroll(nestedScrollConnection)

        ) {
            item {
                VideoInfoItem(mediaController?.mediaMetadata?.title.toString())
            }
            item {
                PitchControlItem()
            }
            item {
                TempoControlItem()
            }
            items(20) { index ->
                RelatedVideoItem(index)
            }
        }


    }

}





@Composable
fun RelatedVideoItem(index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp, 80.dp)
                .background(Color.Gray)
        )

        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text("Related Video $index" )
            Text("Channel Name", )
            Text("1M views • 2 days ago")
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


