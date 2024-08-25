package com.example.transpose.ui.screen.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.navhost.LibraryNavHost
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.utils.LogComposableLifecycle
import com.example.transpose.utils.Logger

@Composable
fun LibraryMainScreen(
    navigationViewModel: NavigationViewModel,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
    onBackButtonClick: () -> Unit
){
    LogComposableLifecycle(screenName = "LibraryMainScreen")


    val libraryCurrentRoute by navigationViewModel.libraryNavCurrentRoute.collectAsState()
    val navController = rememberNavController()
    var canPopNested by remember { mutableStateOf(true) }


    BackHandler {
        if (canPopNested) {
            navController.popBackStack()
        } else {
            onBackButtonClick()
        }
    }

    LaunchedEffect(libraryCurrentRoute) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            canPopNested = navController.previousBackStackEntry != null
        }
        if (libraryCurrentRoute != navigationViewModel.libraryPreviousRoute) {
            navController.navigate(libraryCurrentRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            navigationViewModel.libraryPreviousRoute = libraryCurrentRoute
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        LibraryNavHost(
            navController = navController,
            startDestination = Route.Library.MyPlaylist.route,
            navigationViewModel = navigationViewModel,
            mediaViewModel = mediaViewModel,
            mainViewModel = mainViewModel,
            modifier = Modifier.fillMaxSize()
        )
    }

}