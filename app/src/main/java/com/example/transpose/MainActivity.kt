package com.example.transpose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transpose.ui.components.appbar.MainAppBar
import com.example.transpose.ui.components.bottomnavigation.BottomNavigationBar
import com.example.transpose.ui.components.bottomsheet.PlayerBottomSheet
import com.example.transpose.ui.components.bottomsheet.PlayerBottomSheetScaffold
import com.example.transpose.ui.screen.convert.ConvertMainScreen
import com.example.transpose.ui.screen.convert.ConvertViewModel
import com.example.transpose.ui.screen.home.HomeMainScreen
import com.example.transpose.ui.screen.home.HomeViewModel
import com.example.transpose.ui.screen.library.LibraryMainScreen
import com.example.transpose.ui.screen.library.LibraryViewModel
import com.example.transpose.ui.theme.TransposeTheme
import com.example.transpose.utils.LogComposableLifecycle
import com.example.transpose.utils.Logger
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val mainViewModel: MainViewModel by viewModels()
        val mediaControlViewModel: MediaViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TransposeTheme {
                MainScreen(mainViewModel = mainViewModel, mediaViewModel = mediaControlViewModel)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    fun MainScreen(
        mainViewModel: MainViewModel, mediaViewModel: MediaViewModel
    ) {

        val homeViewModel: HomeViewModel by viewModels()
        val convertViewModel: ConvertViewModel by viewModels()
        val libraryViewModel: LibraryViewModel by viewModels()
        val navigationViewModel: NavigationViewModel by viewModels()

        val searchWidgetState by mainViewModel.searchWidgetState.collectAsState()
        val searchTextState by mainViewModel.searchTextState.collectAsState()
        val suggestionKeywords by mainViewModel.suggestionKeywords.collectAsState()
        val isSearchBarActive by mainViewModel.isSearchBarActive.collectAsState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


        val navController = rememberNavController()
        val mainCurrentRoute by navigationViewModel.mainNavCurrentRoute.collectAsState()


        val mediaController by mediaViewModel.mediaController.collectAsState()


        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val behaviorConsumed =
                        scrollBehavior.nestedScrollConnection.onPreScroll(available, source)
                    return behaviorConsumed
                }
            }
        }
        LogComposableLifecycle(screenName = "MainScreen")

        BackHandler {
            when (mainCurrentRoute) {
                Route.Home.route -> {}
                Route.Convert.route -> {
                    navigationViewModel.changeMainCurrentRoute(Route.Home.route)
                }

                Route.Library.route -> {
                    navigationViewModel.changeMainCurrentRoute(Route.Home.route)
                }
            }
        }


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


        Scaffold(containerColor = Color.White, bottomBar = {
            BottomNavigationBar(
                navigationViewModel = navigationViewModel, searchWidgetState = searchWidgetState
            )
        })
        { innerPadding ->
            val bottomPadding = innerPadding.calculateBottomPadding() + 56.dp

            PlayerBottomSheetScaffold(
                topAppBar = {
                    MainAppBar(
                        searchWidgetState = searchWidgetState,
                        searchTextState = searchTextState,
                        onTextChange = {
                            mainViewModel.updateSearchTextState(it)
                            mainViewModel.getSuggestionKeyword(it)
                        },
                        onTextClearClicked = {
                            mainViewModel.clearSuggestionKeywords()
                            mainViewModel.updateSearchTextState("")
                        },
                        onCloseClicked = { mainViewModel.closeSearchBar() },
                        onSearchClicked = {
                            when (mainCurrentRoute) {
                                Route.Home.route -> {
                                    navigationViewModel.changeHomeCurrentRoute(
                                        Route.Home.SearchResult.createRoute(
                                            it
                                        )
                                    )
                                }

                                Route.Convert.route -> {}
                                Route.Library.route -> {}
                            }
                        },
                        onSearchTriggered = { mainViewModel.openSearchBar() },
                        suggestionKeywords = suggestionKeywords,
                        isSearchBarExpanded = isSearchBarActive,
                        onSearchBarActiveChanged = { mainViewModel.updateIsSearchBarExpanded(it) },
                        scrollBehavior = scrollBehavior
                    )
                },
                playerBottomSheet = {
                    PlayerBottomSheet(
                        mediaController = mediaController,
                    )
                },
                innerPadding = innerPadding
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Route.Home.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .nestedScroll(nestedScrollConnection)
                ) {

                    composable(route = Route.Home.route) {
                        HomeMainScreen(navigationViewModel = navigationViewModel,
                            homeViewModel = homeViewModel,
                            mediaViewModel = mediaViewModel,
                            onBackButtonClick = { navController.popBackStack() }

                        )
                    }
                    composable(route = Route.Convert.route) {
                        ConvertMainScreen(
                            navigationViewModel = navigationViewModel,
                            convertViewModel = convertViewModel
                        )
                    }
                    composable(route = Route.Library.route) {
                        LibraryMainScreen(
                            navigationViewModel = navigationViewModel,
                            libraryViewModel = libraryViewModel
                        )
                    }
                }
            }


        }


    }
}

