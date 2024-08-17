package com.example.transpose.ui.screen.convert

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.transpose.NavigationViewModel
import com.example.transpose.utils.LogComposableLifecycle

@Composable
fun ConvertMainScreen(
    navigationViewModel: NavigationViewModel,
    convertViewModel: ConvertViewModel
){
    LogComposableLifecycle(screenName = "ConvertMainScreen")

    Box(
        modifier = Modifier.fillMaxSize()
    ){

    }
}