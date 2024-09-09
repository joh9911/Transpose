package com.example.transpose.navigation.navgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.ui.screen.home.home_playlist.HomePlaylistScreen
import com.example.transpose.ui.screen.home.playlist_item.HomePlaylistItemScreen
import com.example.transpose.ui.screen.home.search_result.HomeSearchResultScreen

fun NavGraphBuilder.homeNavGraph(
    navigationViewModel: NavigationViewModel,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel
) {

    composable(Route.Home.Playlist.route) {
        HomePlaylistScreen(mainViewModel = mainViewModel, homePlaylistViewModel = hiltViewModel(), navigationViewModel = navigationViewModel)
    }
    composable(
        route = Route.Home.PlaylistItem.route,
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId")
        HomePlaylistItemScreen(
            mainViewModel = mainViewModel,
            homePlaylistItemViewModel = hiltViewModel(),
            navigationViewModel = hiltViewModel(),
            mediaViewModel = mediaViewModel,
            itemId = itemId
        )
    }
    composable(
        route = Route.Home.SearchResult.route,
        arguments = listOf(navArgument("query") { type = NavType.StringType })
    ) { backStackEntry ->
        val query = backStackEntry.arguments?.getString("query")
        HomeSearchResultScreen(
            homeSearchResultViewModel = hiltViewModel(),
            navigationViewModel = hiltViewModel(),
            mediaViewModel = mediaViewModel,
            mainViewModel = mainViewModel,
            query = query
        )
    }
}