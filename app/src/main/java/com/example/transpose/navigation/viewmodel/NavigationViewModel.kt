package com.example.transpose.navigation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _mainNavCurrentRoute = MutableStateFlow(Route.Home.route)
    val mainNavCurrentRoute = _mainNavCurrentRoute.asStateFlow()

    var mainNavPreviousRoute = Route.Home.route

    fun changeMainCurrentRoute(route: String) {
        _mainNavCurrentRoute.value = route
    }


    private val _homeNavCurrentRoute = MutableStateFlow(Route.Home.Playlist.route)
    val homeNavCurrentRoute = _homeNavCurrentRoute.asStateFlow()

    private val _resetHomeNavigation = MutableStateFlow(false)
    val resetHomeNavigation = _resetHomeNavigation.asStateFlow()

    fun changeHomeCurrentRoute(route: String) {
        _homeNavCurrentRoute.value = route
    }

    fun onResetHomeNavigationHandled() {
        _resetHomeNavigation.value = false
    }


    private val _convertNavCurrentRoute = MutableStateFlow(Route.Convert.AudioEdit.route)
    val convertNavCurrentRoute = _convertNavCurrentRoute.asStateFlow()

    private val _resetConvertNavigation = MutableStateFlow(false)
    val resetConvertNavigation = _resetConvertNavigation.asStateFlow()

    fun changeConvertCurrentRoute(route: String) {
        _convertNavCurrentRoute.value = route
    }

    fun onResetConvertNavigationHandled() {
        _resetConvertNavigation.value = false
    }

    private val _libraryNavCurrentRoute = MutableStateFlow(Route.Library.MyPlaylist.route)
    val libraryNavCurrentRoute = _libraryNavCurrentRoute.asStateFlow()

    private val _resetLibraryNavigation = MutableStateFlow(false)
    val resetLibraryNavigation = _resetLibraryNavigation.asStateFlow()

    fun changeLibraryCurrentRoute(route: String) {
        _libraryNavCurrentRoute.value = route
    }

    fun onResetLibraryNavigationHandled() {
        _resetLibraryNavigation.value = false
    }


    fun resetNavigationFor(route: String) {
        when (route) {
            Route.Home.route -> _resetHomeNavigation.value = true
            Route.Convert.route -> _resetConvertNavigation.value = true
            Route.Library.route -> _resetLibraryNavigation.value = true
        }
    }


}

