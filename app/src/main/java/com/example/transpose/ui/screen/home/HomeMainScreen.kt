package com.example.transpose.ui.screen.home

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transpose.MainViewModel
import com.example.transpose.ui.screen.home.homeplaylist.HomePlaylistScreen
import com.example.transpose.ui.screen.home.playlistitem.HomePlaylistItemScreen
import com.example.transpose.ui.screen.home.searchresult.HomeSearchResultScreen
import com.example.transpose.utils.LogComposableLifecycle

@Composable
fun HomeMainScreen(
    homeViewModel: HomeViewModel
){
    val navController = rememberNavController()
    LogComposableLifecycle(screenName = "HomeMainScreen")
    Box(modifier = Modifier.fillMaxSize()

    ){
        NavHost(
            navController = navController, startDestination = "home/playlist", modifier = Modifier
                .fillMaxSize()

        ) {
            composable(route = "home/playlist"){
                HomePlaylistScreen(homeViewModel = homeViewModel)
            }
            composable(route = "home/playlist_item"){
                HomePlaylistItemScreen(homeViewModel = homeViewModel)
            }
            composable(route = "home/search_result"){
                HomeSearchResultScreen(viewModel = homeViewModel)
            }


        }
    }
}