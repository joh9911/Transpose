package com.example.transpose.ui.screen.library.my_playlist_item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel

@Composable
fun LibraryMyPlaylistItemScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    libraryMyPlaylistItemViewModel: LibraryMyPlaylistItemViewModel,
    itemId: String?
){
    Text(text = "LibraryMyPlaylistItemScreen")
}