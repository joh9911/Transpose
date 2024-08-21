package com.example.transpose.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.NavigationViewModel
import com.example.transpose.navigation.navgraph.homeNavGraph

@Composable
fun HomeNavHost(navController: NavHostController, startDestination: String, modifier: Modifier, navigationViewModel: NavigationViewModel, mediaViewModel: MediaViewModel){
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        homeNavGraph(navigationViewModel = navigationViewModel, mediaViewModel = mediaViewModel)
    }
}