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
import androidx.navigation.compose.rememberNavController
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.NavigationViewModel
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
    val navController = rememberNavController()
    var canPopNested by remember { mutableStateOf(true) }


    LogComposableLifecycle(screenName = "HomeMainScreen")

    BackHandler {
        if (canPopNested) {
            navController.popBackStack()
        } else {
            onBackButtonClick()
        }
    }

    LaunchedEffect(homeNavCurrentRoute) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            canPopNested = navController.previousBackStackEntry != null
        }
        if (homeNavCurrentRoute != navigationViewModel.homeNavPreviousRoute) {
            Logger.d("되어야 하는데? $homeNavCurrentRoute")
            navController.navigate(homeNavCurrentRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            navigationViewModel.homeNavPreviousRoute = homeNavCurrentRoute
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