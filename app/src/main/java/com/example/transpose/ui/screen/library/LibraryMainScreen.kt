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
import androidx.navigation.compose.currentBackStackEntryAsState
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

    val libraryCurrentRoute by navigationViewModel.libraryNavCurrentRoute.collectAsState()
    val resetLibraryNavigation by navigationViewModel.resetLibraryNavigation.collectAsState()

    val navController = rememberNavController()
    val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()


    LaunchedEffect(resetLibraryNavigation) {
        if (resetLibraryNavigation) {
            navController.popBackStack(Route.Library.MyPlaylist.route, inclusive = false)
            navigationViewModel.changeLibraryCurrentRoute(Route.Library.MyPlaylist.route)
            navigationViewModel.onResetLibraryNavigationHandled()
        }
    }

    LaunchedEffect(currentBackStackEntryAsState) {
        currentBackStackEntryAsState?.destination?.route?.let {
            navigationViewModel.changeLibraryCurrentRoute(it)
        }
    }

    BackHandler {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        } else {
            onBackButtonClick()
        }
    }

    LaunchedEffect(libraryCurrentRoute) {
        if (navController.currentDestination?.route != libraryCurrentRoute){
            navController.navigate(libraryCurrentRoute) {
                restoreState = true
            }
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