package com.example.transpose.utils

sealed class MainBackHandlerState{
    data object Default : MainBackHandlerState()
    data object SearchOpened : MainBackHandlerState()
    data object BottomSheetExpanded : MainBackHandlerState()
    data object DialogShown : MainBackHandlerState()
}