package com.example.transpose.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.utils.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    modifier: Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val currentPlaylistItems by mediaViewModel.currentPlaylistItems.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            skipHiddenState = false
        )
    )
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        snapshotFlow { scaffoldState.bottomSheetState.currentValue }.collect {

            Logger.d("playlist $it")
        }
    }
    BoxWithConstraints(modifier = modifier) {

        val parentHeight = maxHeight



        BottomSheetScaffold(
            sheetContainerColor = Color.White,
            sheetPeekHeight = 50.dp,
            sheetContent = {
                if (currentPlaylistItems.isEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight()
                            .background(Color.White)
                    ) {
                        // Fixed header
                        Column {
                            // Playlist title and close button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PlaylistTitle",
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_close_24),
                                        contentDescription = "Close"
                                    )
                                }
                            }

                            // Play mode and playlist info
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_repeat_24),
                                        contentDescription = "Repeat",
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                IconButton(onClick = {}, modifier = Modifier.padding(start = 8.dp)) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_shuffle_24),
                                        contentDescription = "Shuffle",
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }

                        // Scrollable content
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {

                        }
                    }
                }

            }, scaffoldState = scaffoldState,
        ) { paddingValues ->
            content(paddingValues)
        }
    }
    
}


