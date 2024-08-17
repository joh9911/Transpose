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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.transpose.NavigationViewModel
import com.example.transpose.Route
import com.example.transpose.ui.screen.home.homeplaylist.HomePlaylistScreen
import com.example.transpose.ui.screen.home.playlistitem.HomePlaylistItemScreen
import com.example.transpose.ui.screen.home.searchresult.HomeSearchResultScreen
import com.example.transpose.utils.LogComposableLifecycle

@Composable
fun HomeMainScreen(
    navigationViewModel: NavigationViewModel,
    homeViewModel: HomeViewModel,
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
        NavHost(
            navController = navController, startDestination = "home/playlist", modifier = Modifier
                .fillMaxSize()

        ) {
            composable(route = "home/playlist") {
                HomePlaylistScreen(homeViewModel = homeViewModel, navigationViewModel = navigationViewModel)
            }
            composable(route = Route.Home.PlaylistItem.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                HomePlaylistItemScreen(homeViewModel = homeViewModel, itemId = itemId, navigationViewModel = navigationViewModel)
            }
            composable(
                route = Route.Home.SearchResult.route,
                arguments = listOf(navArgument("query") { type = NavType.StringType })
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query")
                HomeSearchResultScreen(viewModel = homeViewModel, query, navigationViewModel = navigationViewModel)
            }


        }
    }
}