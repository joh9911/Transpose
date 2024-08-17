package com.example.transpose.ui.screen.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.transpose.NavigationViewModel
import com.example.transpose.utils.LogComposableLifecycle

@Composable
fun LibraryMainScreen(
    navigationViewModel: NavigationViewModel,
    libraryViewModel: LibraryViewModel
){
    LogComposableLifecycle(screenName = "LibraryMainScreen")

    Box(
        modifier = Modifier.fillMaxSize()
    )

}