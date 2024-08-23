package com.example.transpose.navigation.navgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.NavigationViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.ui.screen.convert.ConvertMainScreen
import com.example.transpose.ui.screen.convert.ConvertViewModel
import com.example.transpose.ui.screen.home.HomeMainScreen
import com.example.transpose.ui.screen.library.LibraryMainScreen
import com.example.transpose.ui.screen.library.LibraryViewModel

fun NavGraphBuilder.mainNavGraph(
    onBackButtonClick: () -> Unit,
    navigationViewModel: NavigationViewModel,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel
) {
    composable(Route.Home.route) {
        HomeMainScreen(
            mediaViewModel = mediaViewModel,
            navigationViewModel = navigationViewModel,
            mainViewModel = mainViewModel,
            onBackButtonClick = onBackButtonClick
        )
    }
    composable(Route.Convert.route){
        ConvertMainScreen(
            mediaViewModel = mediaViewModel,
            navigationViewModel = navigationViewModel,
            mainViewModel = mainViewModel,
            onBackButtonClick = onBackButtonClick
        )
    }
    composable(Route.Library.route){
        LibraryMainScreen()
    }

}