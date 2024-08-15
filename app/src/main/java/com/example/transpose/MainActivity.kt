package com.example.transpose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transpose.ui.components.appbar.MainAppBar
import com.example.transpose.ui.components.bottomnavigation.BottomNavigationBar
import com.example.transpose.ui.screen.convert.ConvertMainScreen
import com.example.transpose.ui.screen.home.HomeMainScreen
import com.example.transpose.ui.screen.home.HomeViewModel
import com.example.transpose.ui.screen.home.homeplaylist.HomePlaylistScreen
import com.example.transpose.ui.screen.library.LibraryMainScreen
import com.example.transpose.ui.theme.TransposeTheme
import com.example.transpose.utils.LogComposableLifecycle
import com.example.transpose.utils.Logger
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val mainViewModel: MainViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TransposeTheme {
                MainScreen(mainViewModel = mainViewModel)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(
        mainViewModel: MainViewModel
    ) {

        val homeViewModel: HomeViewModel by viewModels()
        val navigationViewModel: NavigationViewModel by viewModels()

        val searchWidgetState by mainViewModel.searchWidgetState.collectAsState()
        val searchTextState by mainViewModel.searchTextState.collectAsState()
        val suggestionKeywords by mainViewModel.suggestionKeywords.collectAsState()
        val isSearchBarActive by mainViewModel.isSearchBarActive.collectAsState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


        val navController = rememberNavController()
        val mainCurrentRoute by navigationViewModel.mainNavCurrentRoute.collectAsState()

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val behaviorConsumed = scrollBehavior.nestedScrollConnection.onPreScroll(available, source)
                    return behaviorConsumed
                }
            }
        }
        LogComposableLifecycle(screenName = "MainScreen")


        LaunchedEffect(mainCurrentRoute) {
            if (mainCurrentRoute != navigationViewModel.mainNavPreviousRoute) {
                navController.navigate(mainCurrentRoute) {
                    // 백 스택 관리
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                navigationViewModel.mainNavPreviousRoute = mainCurrentRoute
            }
        }


        Scaffold(
            containerColor = Color.White,
            topBar = {
                MainAppBar(
                    searchWidgetState = searchWidgetState,
                    searchTextState = searchTextState,
                    onTextChange = {
                        mainViewModel.updateSearchTextState(it)
                        mainViewModel.getSuggestionKeyword(it)
                    },
                    onTextClearClicked = {mainViewModel.clearSuggestionKeywords()
                                         mainViewModel.updateSearchTextState("")},
                    onCloseClicked = { mainViewModel.closeSearchBar() },
                    onSearchClicked = {  },
                    onSearchTriggered = { mainViewModel.openSearchBar() },
                    suggestionKeywords = suggestionKeywords,
                    isSearchBarExpanded = isSearchBarActive,
                    onSearchBarActiveChanged = {mainViewModel.updateIsSearchBarExpanded(it)},
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomNavigationBar(navigationViewModel = navigationViewModel)
            }


        ) { innerPadding ->

            NavHost(
                navController = navController, startDestination = Route.Home.route, modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .nestedScroll(nestedScrollConnection)

            ) {

                composable(route = Route.Home.route) {
                    HomeMainScreen(homeViewModel = homeViewModel)
                }
                composable(route = Route.Convert.route){
                    ConvertMainScreen()
                }
                composable(route = Route.Library.route){
                    LibraryMainScreen()
                }
            }

        }
    }
}

