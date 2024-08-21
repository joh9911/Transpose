package com.example.transpose.navigation.navgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.NavigationViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.ui.screen.home.homeplaylist.HomePlaylistScreen
import com.example.transpose.ui.screen.home.playlistitem.HomePlaylistItemScreen
import com.example.transpose.ui.screen.home.searchresult.HomeSearchResultScreen

fun NavGraphBuilder.homeNavGraph(
     navigationViewModel: NavigationViewModel,
     mediaViewModel: MediaViewModel
) {

    composable(Route.Home.Playlist.route) {
        HomePlaylistScreen(homePlaylistViewModel = hiltViewModel(), navigationViewModel = navigationViewModel)
    }
    composable(
        route = Route.Home.PlaylistItem.route,
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId")
        HomePlaylistItemScreen(
            homePlaylistItemViewModel = hiltViewModel(),
            navigationViewModel = hiltViewModel(),
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
            query = query
        )
    }
}