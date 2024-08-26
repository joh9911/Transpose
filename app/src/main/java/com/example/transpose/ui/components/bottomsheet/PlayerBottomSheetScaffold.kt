package com.example.transpose.ui.components.bottomsheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.components.appbar.SearchWidgetState
import com.example.transpose.utils.Logger
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheetScaffold(
    topAppBar: @Composable() (() -> Unit)? = null,
    innerPadding: PaddingValues,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
    content: @Composable (PaddingValues) -> Unit,

    ) {
    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val normalizedOffset by mainViewModel.normalizedOffset.collectAsState()
    val searchWidgetState by mainViewModel.searchWidgetState.collectAsState()


    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    val screenHeightPx = with(density) { screenHeightDp.toPx() }

    val searchBarClosedSheetPeekHeight = innerPadding.calculateBottomPadding() + 56.dp
    val searchBarOpenedSheetPeekHeight = getNavigationBarHeightDp() + 56.dp

    val sheetPeekHeight = when (bottomSheetState) {
        SheetValue.Hidden -> 0.dp
        else -> if (searchWidgetState == SearchWidgetState.CLOSED) {
            searchBarClosedSheetPeekHeight
        } else {
            searchBarOpenedSheetPeekHeight
        }
    }

    val scaffoldBottomPadding = innerPadding.calculateBottomPadding() + when {
        // 완전히 펼쳐진 상태 (Expanded)
        normalizedOffset >= 1.0f -> 56.dp
        // PartiallyExpanded 상태
        normalizedOffset in 0.0f..1.0f -> 56.dp
        // Hidden으로 가는 중
        normalizedOffset in -1.0f..0.0f -> {
            val progress = -normalizedOffset // 0에서 1 사이의 값으로 변환
            (56 * (1 - progress)).coerceIn(0f, 56f).dp
        }
        // Hidden 상태
        else -> 0.dp
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = bottomSheetState,
            skipHiddenState = false

        )
    )


    LaunchedEffect(bottomSheetState) {
        when (bottomSheetState) {
            SheetValue.Hidden -> scaffoldState.bottomSheetState.hide()
            SheetValue.Expanded -> scaffoldState.bottomSheetState.expand()
            SheetValue.PartiallyExpanded -> scaffoldState.bottomSheetState.partialExpand()
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow {
            transformOffset(scaffoldState.bottomSheetState.requireOffset() / screenHeightPx)
        }
            .collect { normalizedOffset ->
                if (normalizedOffset <= -1f){
                    mainViewModel.hideBottomSheet()
                }
                Logger.d("normalizedOffset $normalizedOffset")
                mainViewModel.updateNormalizedOffset(normalizedOffset)
            }
    }
    BottomSheetScaffold(
        sheetContainerColor = Color.White,
        scaffoldState = scaffoldState,
        modifier = Modifier
            .padding(bottom = scaffoldBottomPadding),
        sheetContent = {
            PlayerBottomSheet(
                mediaViewModel = mediaViewModel,
                mainViewModel = mainViewModel,
            )
        },
        sheetShape = RectangleShape,
        sheetPeekHeight = sheetPeekHeight,
        topBar = topAppBar,
        sheetSwipeEnabled = true,
        sheetDragHandle = null
    ) { playerBottomSheetInnerPadding ->
        content(playerBottomSheetInnerPadding)
    }
}

private fun transformOffset(rawOffset: Float): Float {
    return when {
        rawOffset <= 0f -> 1f // Fully expanded
        rawOffset < 0.89763963f -> {
            // Map 0..0.887245 to 1..0
            1f - (rawOffset / 0.89763963f)
        }

        rawOffset <= 1.1053541f -> {
            // Map 0.887245..1.0990753 to 0..-1
            -(rawOffset - 0.89763963f) / (1.1053541f - 0.89763963f)
        }

        else -> -1f // Fully hidden
    }
}

@Composable
fun getNavigationBarHeightDp(): Dp {
    val density = LocalDensity.current
    return with(density) {
        WindowInsets.navigationBars.getBottom(density).toDp()
    }
}

private fun Modifier.nestedScrollIfSheetGesturesEnabled(
    connection: NestedScrollConnection,
    dispatcher: NestedScrollDispatcher? = null,
    sheetGesturesEnabled: Boolean = true
): Modifier {
    return this.let {
        if (sheetGesturesEnabled) {
            it.nestedScroll(connection, dispatcher)
        } else {
            it
        }
    }
}