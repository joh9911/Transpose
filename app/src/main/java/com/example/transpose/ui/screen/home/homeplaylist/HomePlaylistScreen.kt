package com.example.transpose.ui.screen.home.homeplaylist

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.ui.common.UiState
import com.example.transpose.ui.screen.home.HomeViewModel
import com.example.transpose.utils.Logger
import kotlinx.coroutines.async

@Composable
fun HomePlaylistScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val nationalPlaylistState by viewModel.nationalPlaylistState.collectAsState()
    val recommendedPlaylistState by viewModel.recommendedPlaylistState.collectAsState()
    val typedPlaylistState by viewModel.typedPlaylistState.collectAsState()

    LaunchedEffect(key1 = true) {
        async { viewModel.fetchNationalPlaylists() }
        async { viewModel.fetchRecommendedPlaylists() }
        async { viewModel.fetchTypedPlaylists() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            PlaylistSection(
                title = "국가별 플레이리스트",
                playlistState = nationalPlaylistState
            ) { playlist ->
                NationalPlaylistItem(
                    playlistData = playlist,
                    onClick = {}
                )
            }
        }
        item {
            PlaylistSection(
                title = "추천 플레이리스트",
                playlistState = recommendedPlaylistState
            ) { playlist ->
                RegularPlaylistItem(
                    playlistData = playlist,
                    onClick = {}
                )
            }
        }
        item {
            PlaylistSection(
                title = "취향별 플레이리스트",
                playlistState = typedPlaylistState
            ) { playlist ->
                RegularPlaylistItem(
                    playlistData = playlist,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun PlaylistSection(
    title: String,
    playlistState: HomeViewModel.PlaylistState,
    itemContent: @Composable (NewPipePlaylistData) -> Unit
) {
    Column{
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        when (playlistState.uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                ErrorMessage(
                    isVisible = true,
                    message = (playlistState.uiState).message,
                    onRefresh = {} // 여기에 적절한 리프레시 함수를 넣어주세요
                )
            }
            is UiState.Success -> {
                if (playlistState.items.isEmpty()) {
                    Text(
                        text = "No playlists available",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            count = playlistState.items.size,
                            key = { index -> playlistState.items[index].id }
                        ) { index ->
                            Logger.d("Rendering playlist item at index $index: ${playlistState.items[index].title}")
                            itemContent(playlistState.items[index])
                        }
                    }
                }
            }
            is UiState.Initial -> {
                Text(
                    text = "Ready to load playlists",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun ErrorMessage(
    isVisible: Boolean,
    message: String,
    onRefresh: () -> Unit
) {
    if (isVisible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRefresh) {
                Text(text = "다시로딩")
            }
        }
    }
}
