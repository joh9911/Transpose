package com.example.transpose.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.NavigationViewModel
import com.example.transpose.navigation.navgraph.mainNavGraph
import com.example.transpose.ui.screen.convert.ConvertViewModel
import com.example.transpose.ui.screen.library.LibraryViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: String,
    navigationViewModel: NavigationViewModel,
    mediaViewModel: MediaViewModel,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        mainNavGraph(
            navigationViewModel = navigationViewModel,
            mediaViewModel = mediaViewModel,
            onBackButtonClick =
            { navController.popBackStack() }
        )
    }
}