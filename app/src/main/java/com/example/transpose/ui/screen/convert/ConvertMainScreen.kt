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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.navhost.ConvertNavHost
import com.example.transpose.utils.LogComposableLifecycle
import com.example.transpose.utils.Logger

@Composable
fun ConvertMainScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    onBackButtonClick: () -> Unit
){

    val resetConvertNavigation by navigationViewModel.resetConvertNavigation.collectAsState()
    val convertNavCurrentRoute by navigationViewModel.convertNavCurrentRoute.collectAsState()
    val navController = rememberNavController()
    val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()

    BackHandler {
        Logger.d("HomeMainScreen BackHandler")
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        } else {
            onBackButtonClick()
        }
    }

    LaunchedEffect(resetConvertNavigation) {
        if (resetConvertNavigation) {
            navController.popBackStack(Route.Convert.AudioEdit.route, inclusive = false)
            navigationViewModel.changeLibraryCurrentRoute(Route.Convert.AudioEdit.route)
            navigationViewModel.onResetLibraryNavigationHandled()
        }
    }

    LaunchedEffect(key1 = currentBackStackEntryAsState) {
        currentBackStackEntryAsState?.destination?.route?.let {
            navigationViewModel.changeHomeCurrentRoute(it)
        }
    }

    LaunchedEffect(convertNavCurrentRoute) {
        if (navController.currentDestination?.route != convertNavCurrentRoute){
            navController.navigate(convertNavCurrentRoute) {
                restoreState = true
            }
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