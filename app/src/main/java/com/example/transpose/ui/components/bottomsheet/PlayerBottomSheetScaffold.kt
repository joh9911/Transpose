package com.example.transpose.ui.components.bottomsheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SheetValue
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.components.appbar.SearchWidgetState
import com.example.transpose.utils.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheetScaffold(
    topAppBar: @Composable() (() -> Unit)? = null,
    innerPadding: PaddingValues,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
    content: @Composable (PaddingValues) -> Unit,

    ) {

    val requiredOffset by mainViewModel.requiredOffset.collectAsState()
    val searchWidgetState by mainViewModel.searchWidgetState.collectAsState()
    val isBottomSheetDraggable by mainViewModel.isBottomSheetDraggable.collectAsState()

    val searchBarClosedSheetPeekHeight = innerPadding.calculateBottomPadding() + 56.dp
    val searchBarOpenedSheetPeekHeight = getNavigationBarHeightDp() + 56.dp

    val scaffoldBottomPadding = innerPadding.calculateBottomPadding() + when {
        requiredOffset <= 0f -> 56.dp // 완전히 펼쳐진 상태
        requiredOffset in 0f..1950f -> 56.dp // PartiallyExpanded 상태 유지
        requiredOffset in 1950f..2400f -> {
            // PartiallyExpanded에서 Hidden으로 가는 중
            val progress = (requiredOffset - 1950f) / 450f
            (56 * (1 - progress * 3)).coerceIn(0f, 56f).dp
        }

        else -> 0.dp // Hidden 상태
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            skipHiddenState = false

        )
    )



    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow {
            scaffoldState.bottomSheetState.requireOffset()
        }
            .collect { offset ->
                Logger.d("프로그래스 $offset")
                mainViewModel.updateRequiredOffset(offset)
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
        sheetPeekHeight = if (searchWidgetState == SearchWidgetState.CLOSED) searchBarClosedSheetPeekHeight else searchBarOpenedSheetPeekHeight,
        topBar = topAppBar,
        sheetSwipeEnabled = true,
        sheetDragHandle = null
    ) { playerBottomSheetInnerPadding ->
        content(playerBottomSheetInnerPadding)
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