package com.example.transpose.ui.screen.convert

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
import com.example.transpose.navigation.navhost.ConvertNavHost
import com.example.transpose.navigation.navhost.HomeNavHost
import com.example.transpose.utils.LogComposableLifecycle
import com.example.transpose.utils.Logger

@Composable
fun ConvertMainScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    onBackButtonClick: () -> Unit
){
    LogComposableLifecycle(screenName = "ConvertMainScreen")


    val convertNavCurrentRoute by navigationViewModel.convertNavCurrentRoute.collectAsState()
    val navController = rememberNavController()
    var canPopNested by remember { mutableStateOf(true) }

    BackHandler {
        if (canPopNested) {
            navController.popBackStack()
        } else {
            onBackButtonClick()
        }
    }

    LaunchedEffect(convertNavCurrentRoute) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            canPopNested = navController.previousBackStackEntry != null
        }
        if (convertNavCurrentRoute != navigationViewModel.convertPreviousRoute) {
            navController.navigate(convertNavCurrentRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            navigationViewModel.convertPreviousRoute = convertNavCurrentRoute
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ConvertNavHost(
            navController = navController,
            startDestination = Route.Convert.AudioEdit.route,
            navigationViewModel = navigationViewModel,
            mediaViewModel = mediaViewModel,
            mainViewModel = mainViewModel,
            modifier = Modifier.fillMaxSize()
        )

    }
}