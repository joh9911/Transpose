package com.example.transpose.ui.screen.home

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
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.navhost.HomeNavHost
import com.example.transpose.utils.LogComposableLifecycle
import com.example.transpose.utils.Logger

@Composable
fun HomeMainScreen(
    navigationViewModel: NavigationViewModel,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel,
    onBackButtonClick: () -> Unit
) {

    val homeNavCurrentRoute by navigationViewModel.homeNavCurrentRoute.collectAsState()
    val resetLibraryNavigation by navigationViewModel.resetHomeNavigation.collectAsState()
    val navController = rememberNavController()
    val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()



    LaunchedEffect(resetLibraryNavigation) {
        if (resetLibraryNavigation) {
            navController.popBackStack(Route.Home.Playlist.route, inclusive = false)
            navigationViewModel.changeHomeCurrentRoute(Route.Home.Playlist.route)
            navigationViewModel.onResetHomeNavigationHandled()
        }
    }

    LaunchedEffect(key1 = currentBackStackEntryAsState) {
        currentBackStackEntryAsState?.destination?.route?.let {
            navigationViewModel.changeHomeCurrentRoute(it)
        }
    }

    LaunchedEffect(homeNavCurrentRoute) {
        if (navController.currentDestination?.route != homeNavCurrentRoute){
            navController.navigate(homeNavCurrentRoute) {
                restoreState = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeNavHost(
            navController = navController,
            startDestination = Route.Home.Playlist.route,
            navigationViewModel = navigationViewModel,
            mediaViewModel = mediaViewModel,
            mainViewModel = mainViewModel,
            modifier = Modifier.fillMaxSize()
        )

    }
}