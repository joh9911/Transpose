package com.example.transpose.navigation.navgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.screen.convert.audio_edit.ConvertAudioEditScreen
import com.example.transpose.ui.screen.convert.search_result.ConvertSearchResultScreen

fun NavGraphBuilder.convertNavGraph(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel
) {
    composable(
        route = Route.Convert.AudioEdit.route
    ) {
        ConvertAudioEditScreen(
            mainViewModel = mainViewModel,
            mediaViewModel = mediaViewModel,
            navigationViewModel = navigationViewModel,
            convertAudioEditViewModel = hiltViewModel()
        )
    }
    composable(
        route = Route.Convert.SearchResult.route,
        arguments = listOf(navArgument("query") { type = NavType.StringType })
    ) { backStackEntry ->
        val query = backStackEntry.arguments?.getString("query")

        ConvertSearchResultScreen(
            convertSearchResultViewModel = hiltViewModel(),
            mainViewModel = mainViewModel,
            mediaViewModel = mediaViewModel,
            query = query
        )
    }
}