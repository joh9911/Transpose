package com.example.transpose.navigation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.transpose.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(): ViewModel() {
    private val _mainNavCurrentRoute = MutableStateFlow(Route.Home.route)
    val mainNavCurrentRoute = _mainNavCurrentRoute.asStateFlow()

    var mainNavPreviousRoute = Route.Home.route

    fun changeMainCurrentRoute(route: String){
        _mainNavCurrentRoute.value = route
    }

    private val _homeNavCurrentRoute = MutableStateFlow(Route.Home.Playlist.route)
    val homeNavCurrentRoute = _homeNavCurrentRoute.asStateFlow()

    var homeNavPreviousRoute = Route.Home.Playlist.route

    fun changeHomeCurrentRoute(route: String){
        _homeNavCurrentRoute.value = route
    }

    private val _convertNavCurrentRoute = MutableStateFlow(Route.Convert.AudioEdit.route)
    val convertNavCurrentRoute = _convertNavCurrentRoute.asStateFlow()

    var convertPreviousRoute = Route.Convert.AudioEdit.route

    fun changeConvertCurrentRoute(route: String){
        _convertNavCurrentRoute.value = route
    }

    private val _libraryNavCurrentRoute = MutableStateFlow(Route.Library.MyPlaylist.route)
    val libraryNavCurrentRoute = _libraryNavCurrentRoute.asStateFlow()

    var libraryPreviousRoute = Route.Library.MyPlaylist.route

    fun changeLibraryCurrentRoute(route: String){
        _libraryNavCurrentRoute.value = route
    }

}
