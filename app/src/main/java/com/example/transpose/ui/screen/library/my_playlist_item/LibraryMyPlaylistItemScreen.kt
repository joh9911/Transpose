package com.example.transpose.ui.screen.library.my_playlist_item

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.screen.library.my_playlist_item.items.PlaylistVideoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryMyPlaylistItemScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    libraryMyPlaylistItemViewModel: LibraryMyPlaylistItemViewModel,
    itemId: String?
) {

    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val myPlaylistItems by libraryMyPlaylistItemViewModel.myPlaylistItems.collectAsState()

    LaunchedEffect(itemId) {
        itemId?.let {
            libraryMyPlaylistItemViewModel.getVideosForPlaylist(itemId.toLong())
        }
    }

    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }
    if (myPlaylistItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.playlist_item_empty_text),
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(myPlaylistItems.size) { index ->
            val item = myPlaylistItems[index]
            PlaylistVideoItem(item = item, onClick = {
                mediaViewModel.onMediaItemClick(item)
                mainViewModel.expandBottomSheet()

            }, dropDownMenuClick = {
                itemId?.let {
                    libraryMyPlaylistItemViewModel.deleteVideo(itemId.toLong(), item)
                }
            })
        }
    }


}