package com.example.transpose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.navhost.MainNavHost
import com.example.transpose.ui.components.appbar.MainAppBar
import com.example.transpose.ui.components.bottom_navigation.BottomNavigationBar
import com.example.transpose.ui.components.bottomsheet.PlayerBottomSheetScaffold
import com.example.transpose.ui.theme.TransposeTheme
import com.example.transpose.utils.Logger
import com.example.transpose.utils.constants.AppColors
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TransposeTheme {
                MainScreen(mainViewModel = mainViewModel, mediaViewModel = mediaViewModel)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(
        mainViewModel: MainViewModel, mediaViewModel: MediaViewModel
    ) {
        val systemUiController = rememberSystemUiController()

        val navigationViewModel: NavigationViewModel by viewModels()
        val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
        val searchWidgetState by mainViewModel.searchWidgetState.collectAsState()
        val searchTextState by mainViewModel.searchTextState.collectAsState()
        val suggestionKeywords by mainViewModel.suggestionKeywords.collectAsState()
        val isSearchBarActive by mainViewModel.isSearchBarActive.collectAsState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


        val navController = rememberNavController()
        val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()
        val mainCurrentRoute by navigationViewModel.mainNavCurrentRoute.collectAsState()


        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val behaviorConsumed =
                        scrollBehavior.nestedScrollConnection.onPreScroll(available, source)
                    return behaviorConsumed
                }
            }
        }

        SideEffect {
            systemUiController.setStatusBarColor(
                color = AppColors.StatusBarBackground,
            )

            systemUiController.setNavigationBarColor(
                color = AppColors.BlueBackground,
            )
        }

        BackHandler {
            if (bottomSheetState == SheetValue.Expanded){
                mainViewModel.partialExpandBottomSheet()
            }
            else{
                when (mainCurrentRoute) {
                    Route.Home.route -> { this.moveTaskToBack(true)}
                    Route.Convert.route -> {
                        navigationViewModel.changeMainCurrentRoute(Route.Home.route)
                    }

                    Route.Library.route -> {
                        navigationViewModel.changeMainCurrentRoute(Route.Home.route)
                    }
                }
            }
        }

        LaunchedEffect(key1 = currentBackStackEntryAsState) {
            currentBackStackEntryAsState?.destination?.route?.let {
                navigationViewModel.changeMainCurrentRoute(it)
            }
        }


        LaunchedEffect(mainCurrentRoute) {
            if (navController.currentDestination?.route != mainCurrentRoute){
                navController.navigate(mainCurrentRoute) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }


        Scaffold(containerColor = Color.White, bottomBar = {
            BottomNavigationBar(
                navigationViewModel = navigationViewModel,
                searchWidgetState = searchWidgetState,
                mainViewModel = mainViewModel
            )
        })
        { innerPadding ->
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

                                Route.Convert.route -> {
                                    navigationViewModel.changeConvertCurrentRoute(
                                        Route.Convert.SearchResult.createRoute(
                                            it
                                        )
                                    )
                                }

                                Route.Library.route -> {
                                    navigationViewModel.changeLibraryCurrentRoute(
                                        Route.Library.SearchResult.createRoute(
                                            it
                                        )
                                    )
                                }
                            }
                        },
                        onSearchTriggered = { mainViewModel.openSearchBar() },
                        suggestionKeywords = suggestionKeywords,
                        isSearchBarExpanded = isSearchBarActive,
                        onSearchBarActiveChanged = { mainViewModel.updateIsSearchBarExpanded(it) },
                        scrollBehavior = scrollBehavior,

                        )
                },
                innerPadding = innerPadding,
                mediaViewModel = mediaViewModel,
                mainViewModel = mainViewModel
            ) { playerBottomSheetScaffoldPadding ->
                MainNavHost(
                    navController = navController,
                    startDestination = Route.Home.route,
                    navigationViewModel = navigationViewModel,
                    mediaViewModel = mediaViewModel,
                    mainViewModel = mainViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .nestedScroll(nestedScrollConnection)
                )

            }


        }


    }
}

