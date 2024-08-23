package com.example.transpose.navigation

import androidx.lifecycle.ViewModel
import com.example.transpose.utils.Logger
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

