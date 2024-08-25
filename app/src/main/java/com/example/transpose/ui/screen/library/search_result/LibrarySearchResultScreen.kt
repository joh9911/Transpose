package com.example.transpose.ui.screen.library.search_result

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.screen.convert.search_result.ConvertSearchResultViewModel

@Composable
fun LibrarySearchResultScreen(
    librarySearchResultViewModel: LibrarySearchResultViewModel,
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    query: String?
){
    Text("LibrarySearchResultScreen")
}