package com.example.transpose.ui.screen.library.my_playlist_item

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.viewmodel.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryMyPlaylistItemScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    libraryMyPlaylistItemViewModel: LibraryMyPlaylistItemViewModel,
    itemId: String?
){

    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()

    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }



    Text(text = "LibraryMyPlaylistItemScreen")
}