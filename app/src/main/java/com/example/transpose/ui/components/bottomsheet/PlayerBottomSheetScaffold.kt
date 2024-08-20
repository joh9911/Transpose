package com.example.transpose.ui.components.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.transpose.Route
import com.example.transpose.ui.components.appbar.MainAppBar
import com.example.transpose.utils.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheetScaffold(
    topAppBar: @Composable() (() -> Unit)? = null,
    playerBottomSheet: @Composable() (ColumnScope.() -> Unit),
    innerPadding: PaddingValues,
    content: @Composable (PaddingValues) -> Unit
){

    var bottomPadding by remember { mutableStateOf(56.dp) }

    val sheetPeekHeight = innerPadding.calculateBottomPadding() + 56.dp
    val maxOffset = with(LocalDensity.current) { sheetPeekHeight.toPx() }


    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )

    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow {
            scaffoldState.bottomSheetState.requireOffset()
        }
            .collect { offset ->
                bottomPadding =
                    when {
                        offset <= 0f -> 56.dp // 완전히 펼쳐진 상태
                        offset in 0f..1950f -> 56.dp // PartiallyExpanded 상태 유지
                        offset in 1950f..2400f -> {
                            // PartiallyExpanded에서 Hidden으로 가는 중
                            val progress = (offset - 1950f) / 450f
                            (56 * (1 - progress)).dp
                        }
                        else -> 0.dp // Hidden 상태
                    }
                Logger.d("bottomPadding changed to $bottomPadding (offset: $offset)")
                }



//        snapshotFlow { scaffoldState.bottomSheetState.currentValue }
//            .collect { currentValue ->
//                bottomPadding = when (currentValue) {
//                    SheetValue.PartiallyExpanded -> {
//                        if (scaffoldState.bottomSheetState.targetValue == SheetValue.Hidden) {
//                            0.dp
//                        } else {
//                            56.dp
//                        }
//                    }
//
//                    SheetValue.Hidden -> {
//                        0.dp
//                    }
//
//                    SheetValue.Expanded -> {
//                        56.dp
//                    }
//                }
//                Logger.d("bottomPadding changed to $bottomPadding")
//            }

    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.padding(bottom = bottomPadding),
        sheetContent = playerBottomSheet,
        sheetShape = RectangleShape,
        sheetPeekHeight = sheetPeekHeight,
        topBar = topAppBar
    ) { playerBottomSheetInnerPadding ->
        content(playerBottomSheetInnerPadding)
    }
}