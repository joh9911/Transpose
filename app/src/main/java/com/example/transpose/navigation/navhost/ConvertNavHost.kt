package com.example.transpose.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.navigation.navgraph.convertNavGraph

@Composable
fun ConvertNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier,
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        convertNavGraph(
            mainViewModel = mainViewModel,
            mediaViewModel = mediaViewModel,
            navigationViewModel = navigationViewModel
        )
    }
}