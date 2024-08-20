package com.example.transpose.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import com.example.transpose.utils.Logger
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheet(
    mediaController: MediaController?
) {


    val density = LocalDensity.current.density
    val peekHeightPx = 56 * density
    val defaultHeight = 250.dp

    val defaultHeightPx = with(LocalDensity.current) { defaultHeight.toPx() }

    val scope = rememberCoroutineScope()


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val (mainContainerLayout, playerView, playerThumbnailView, bufferingProgressBar, bottomPlayerCloseButton, bottomPlayerPauseButton, bottomTitleTextView) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(defaultHeight)
                .constrainAs(mainContainerLayout) {
                    top.linkTo(parent.top)
                }
                .background(Color.Blue)

        )

        // playerThumbnailView
        Box(
            modifier = Modifier
                .constrainAs(playerThumbnailView) {
                    top.linkTo(playerView.top)
                    start.linkTo(playerView.start)
                    end.linkTo(playerView.end)
                    bottom.linkTo(playerView.bottom)
                }
                .background(Color.LightGray)
        )

        // bufferingProgressBar
        CircularProgressIndicator(
            modifier = Modifier
                .constrainAs(bufferingProgressBar) {
                    top.linkTo(playerView.top)
                    start.linkTo(playerView.start)
                    end.linkTo(playerView.end)
                    bottom.linkTo(playerView.bottom)
                }
        )

        // centerGuideline
        val centerGuideline = createGuidelineFromTop(0.5f)

        // bottomPlayerCloseButton
        IconButton(
            onClick = { /* Close logic */ },
            modifier = Modifier
                .constrainAs(bottomPlayerCloseButton) {
                    end.linkTo(parent.end)
                    bottom.linkTo(centerGuideline)
                }
        ) {
            Icons.Default.Close
        }

        // bottomPlayerPauseButton
        IconButton(
            onClick = { /* Play/Pause logic */ },
            modifier = Modifier
                .constrainAs(bottomPlayerPauseButton) {
                    end.linkTo(bottomPlayerCloseButton.start, margin = 5.dp)
                    bottom.linkTo(centerGuideline)
                }
        ) {
            Icons.Default.PlayArrow
        }

        // bottomTitleTextView
        Text(
            text = "Video Title",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier
                .constrainAs(bottomTitleTextView) {
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(bottomPlayerPauseButton.start, margin = 12.dp)
                    bottom.linkTo(centerGuideline)
                    width = Dimension.fillToConstraints
                }
        )

        AndroidView(factory = { ctx ->
            PlayerView(ctx).apply {}
        }, update = { view ->
            mediaController?.let { controller ->
                view.player = controller
            } ?: run {
                view.player = null
            }
        }, modifier = Modifier

            .constrainAs(playerView) {
                top.linkTo(mainContainerLayout.top)
                start.linkTo(mainContainerLayout.start)
                end.linkTo(mainContainerLayout.end)
                bottom.linkTo(mainContainerLayout.bottom)
            }
        )

    }

}