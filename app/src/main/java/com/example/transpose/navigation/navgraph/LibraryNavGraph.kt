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
import com.example.transpose.ui.screen.library.my_playlist.LibraryMyPlaylistScreen
import com.example.transpose.ui.screen.library.my_playlist_item.LibraryMyPlaylistItemScreen
import com.example.transpose.ui.screen.library.search_result.LibrarySearchResultScreen

fun NavGraphBuilder.libraryNavGraph(
    navigationViewModel: NavigationViewModel,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel
) {
    composable(route = Route.Library.MyPlaylist.route) {
        LibraryMyPlaylistScreen(
            mainViewModel = mainViewModel,
            mediaViewModel = mediaViewModel,
            navigationViewModel = navigationViewModel,
            libraryMyPlaylistViewModel = hiltViewModel()
        )
    }
    composable(
        route = Route.Library.MyPlaylistItem.route,
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId")

        LibraryMyPlaylistItemScreen(
            mainViewModel = mainViewModel,
            mediaViewModel = mediaViewModel,
            navigationViewModel = navigationViewModel,
            libraryMyPlaylistItemViewModel = hiltViewModel(),
            itemId = itemId
        )
    }
    composable(
        route = Route.Library.SearchResult.route,
        arguments = listOf(navArgument("query") { type = NavType.StringType })
    ) { backStackEntry ->
        val query = backStackEntry.arguments?.getString("query")
        LibrarySearchResultScreen(
            librarySearchResultViewModel = hiltViewModel(),
            mainViewModel = mainViewModel,
            mediaViewModel = mediaViewModel,
            query = query
        )
    }

}