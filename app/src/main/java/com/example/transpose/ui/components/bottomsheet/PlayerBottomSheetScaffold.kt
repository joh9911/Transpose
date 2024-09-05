package com.example.transpose.ui.components.bottomsheet

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.components.appbar.SearchWidgetState
import com.example.transpose.utils.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)
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

    val coroutineScope = rememberCoroutineScope()

    val isKeyboardOpen by keyboardAsState() // true or false

    val hasLaunched = remember { mutableStateOf(false) }



    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val sheetPeekHeight = when (sheetState.currentValue) {
        SheetValue.Hidden -> 0.dp
        else -> if (searchWidgetState == SearchWidgetState.CLOSED) {
            searchBarClosedSheetPeekHeight
        } else {
            searchBarOpenedSheetPeekHeight
        }
    }



    val scaffoldBottomPadding =
        innerPadding.calculateBottomPadding() + if (bottomSheetState == SheetValue.Hidden) {
            0.dp
        } else {
            when {
                // 완전히 펼쳐진 상태 (Expanded)
                normalizedOffset >= 1.0f -> 56.dp
                // PartiallyExpanded 상태
                normalizedOffset in 0.0f..1.0f -> 56.dp
                // Hidden으로 가는 중
                normalizedOffset in -1.0f..0.0f -> {
                    val progress = (-normalizedOffset * 2).coerceIn(0f, 1f) // 0에서 1 사이의 값으로 변환
                    (56 * (1 - progress)).dp
                }
                // Hidden 상태
                else -> 0.dp
            }
        }


    LaunchedEffect(bottomSheetState) {
            when (bottomSheetState) {
                SheetValue.Expanded -> {
                    sheetState.expand()
                }
                SheetValue.PartiallyExpanded -> {

                    sheetState.partialExpand()
                }
                SheetValue.Hidden -> {

                }
            }
    }


    // BottomSheetState의 변경을 ViewModel에 반영
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentValue }.collect {
            when(it){
                SheetValue.Hidden -> {
                    mainViewModel.hideBottomSheet()
                    mediaViewModel.removeCurrentMediaItem()
                }
                SheetValue.Expanded -> mainViewModel.expandBottomSheet()
                SheetValue.PartiallyExpanded -> mainViewModel.partialExpandBottomSheet()
            }
        }
    }




    LaunchedEffect(sheetState) {
        snapshotFlow {
            transformOffset(
                sheetState.requireOffset() / screenHeightPx,
                searchWidgetState = searchWidgetState,
                isKeyboardOpen
            )
        }
            .collect { normalizedOffset ->

                mainViewModel.updateNormalizedOffset(normalizedOffset)

            }
    }

    BottomSheetScaffold(
        sheetContainerColor = Color.White,
        scaffoldState = scaffoldState,
        modifier = Modifier
            .padding(bottom = scaffoldBottomPadding)
            ,
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

private fun transformOffset(
    rawOffset: Float,
    searchWidgetState: SearchWidgetState,
    isKeyboardVisible: Boolean
): Float {
    return when {
        searchWidgetState == SearchWidgetState.OPENED -> {
            when {
                isKeyboardVisible -> {
                    when {
                        rawOffset <= 0.0f -> 1f // Fully expanded
                        rawOffset < 0.5259643f -> {
                            // Map 0..0.5259643 to 1..0
                            1f - (rawOffset / 0.5259643f)
                        }

                        rawOffset < 0.92803687f -> 0f // Partially expanded
                        rawOffset < 1.000806f -> {
                            // Map 0.92803687..1.000806 to 0..-1
                            -(rawOffset - 0.92803687f) / (1.000806f - 0.92803687f)
                        }

                        else -> -1f // Fully hidden
                    }
                }

                else -> {
                    when {
                        rawOffset <= 0.0f -> 1f // Fully expanded
                        rawOffset < 0.92803687f -> {
                            // Map 0..0.92803687 to 1..0
                            1f - (rawOffset / 0.92803687f)
                        }

                        rawOffset < 1.000806f -> {
                            // Map 0.92803687..1.000806 to 0..-1
                            -(rawOffset - 0.92803687f) / (1.000806f - 0.92803687f)
                        }

                        else -> -1f // Fully hidden
                    }
                }
            }
        }

        else -> {
            // 원래의 변환 로직
            when {
                rawOffset <= 0.0f -> 1f // Fully expanded
                rawOffset < 0.8552677f -> {
                    // Map 0..0.8552677 to 1..0
                    1f - (rawOffset / 0.8552677f)
                }

                rawOffset < 1.000806f -> {
                    // Map 0.8552677..1.000806 to 0..-1
                    -(rawOffset - 0.8552677f) / (1.000806f - 0.8552677f)
                }

                else -> -1f // Fully hidden
            }
        }
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

@Composable
fun keyboardAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val view = LocalView.current

    val viewTreeObserver = view.viewTreeObserver

    DisposableEffect(viewTreeObserver) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                true
            } else {
                false
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

@ExperimentalMaterial3Api
val SheetState.shouldShowModalBottomSheet
    get() = isVisible || targetValue == SheetValue.Expanded || targetValue == SheetValue.PartiallyExpanded