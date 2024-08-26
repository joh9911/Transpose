package com.example.transpose.ui.screen.library.my_local_item

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.screen.library.my_local_item.item.LocalFileData
import com.example.transpose.utils.Logger

@Composable
fun LibraryMyLocalItemScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    libraryMyLocalItemViewModel: LibraryMyLocalItemViewModel,
    type: String?
){

    val audioFiles by libraryMyLocalItemViewModel.audioFiles.collectAsState()
    val videoFiles by libraryMyLocalItemViewModel.videoFiles.collectAsState()


    LaunchedEffect(key1 = true) {
        type?.let { type ->
            Logger.d("LibraryMyLocalItemScreen $type")
            when(type){
                "audio" -> libraryMyLocalItemViewModel.loadAudioFiles()
                "video" -> libraryMyLocalItemViewModel.loadVideoFiles()
            }
        }
    }
    type?.let { type ->
        when(type){
            "audio" -> {
                LazyColumn {
                    items(audioFiles.size){ index ->
                        val item = audioFiles[index]
                        LocalFileData(item = item, onClick = {})

                    }
                }
            }
            "video" -> {
                LazyColumn {
                    items(videoFiles.size){ index ->
                        val item = videoFiles[index]
                        LocalFileData(item = item, onClick = {})

                    }
                }
            }
        }
    }


}