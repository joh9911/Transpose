package com.example.transpose

import androidx.lifecycle.ViewModel
import com.example.transpose.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel: ViewModel() {
    private val _mainNavCurrentRoute = MutableStateFlow(Route.Home.route)
    val mainNavCurrentRoute = _mainNavCurrentRoute.asStateFlow()

    var mainNavPreviousRoute = Route.Home.route

    fun changeMainCurrentRoute(route: String){
        Logger.d("changeRoute: $route")
        _mainNavCurrentRoute.value = route
    }

    private val _homeNavCurrentRoute = MutableStateFlow(Route.Home.Playlist.route)
    val homeNavCurrentRoute = _homeNavCurrentRoute.asStateFlow()

    var homeNavPreviousRoute = Route.Home.Playlist.route

    fun changeHomeCurrentRoute(route: String){
        _homeNavCurrentRoute.value = route
    }
}

sealed class Route(val route: String){
    data object Home: Route("home"){
        data object Playlist: Route("home/playlist")
        data object PlaylistItem: Route("home/playlist_item/{itemId}"){
            fun createRoute(itemId: String) = "home/playlist_item/$itemId"
        }
        data object SearchResult: Route("home/search_result/{query}"){
            fun createRoute(query: String) = "home/search_result/$query"
        }
    }
    data object Convert: Route("convert"){
        data object AudioEdit: Route("convert/audio_edit")
        data object SearchResult: Route("convert/search_result"){
            fun createRoute(query: String) = "home.search_result/$query"
        }
    }
    data object Library: Route("library"){
        data object MyPlaylist: Route("library/my_playlist")
        data object MyPlaylistItem: Route("library/my_playlist_item/{itemId}"){
            fun createRoute(itemId: String) = "library/my_playlist_item/$itemId"
        }
        data object SearchResult: Route("library/search_result"){
            fun createRoute(query: String) = "home.search_result/$query"
        }
    }
}