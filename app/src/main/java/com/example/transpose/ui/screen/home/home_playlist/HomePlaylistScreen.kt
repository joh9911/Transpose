package com.example.transpose.ui.screen.home.home_playlist

import RegularPlaylistItem
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.MainViewModel
import com.example.transpose.R
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.data.model.newpipe.NewPipePlaylistData
import com.example.transpose.ui.common.UiState
import com.example.transpose.ui.screen.home.home_playlist.items.NationalPlaylistItem
import com.example.transpose.utils.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePlaylistScreen(
    mainViewModel: MainViewModel,
    homePlaylistViewModel: HomePlaylistViewModel,
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
) {
    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val nationalPlaylistState by homePlaylistViewModel.nationalPlaylistState.collectAsState()
    val recommendedPlaylistState by homePlaylistViewModel.recommendedPlaylistState.collectAsState()
    val typedPlaylistState by homePlaylistViewModel.typedPlaylistState.collectAsState()

    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }

    LaunchedEffect(key1 = true) {
        navigationViewModel.changeHomeCurrentRoute(Route.Home.Playlist.route)
        if (nationalPlaylistState == UiState.Initial)
            homePlaylistViewModel.fetchNationalPlaylists()
        if (recommendedPlaylistState == UiState.Initial)
            homePlaylistViewModel.fetchRecommendedPlaylists()
        if (typedPlaylistState == UiState.Initial)
            homePlaylistViewModel.fetchTypedPlaylists()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            PlaylistSection(
                title = stringResource(id = R.string.nation_playlist_text),
                playlistState = nationalPlaylistState
            ) { playlist ->
                NationalPlaylistItem(
                    playlistData = playlist,
                    onClick = { navigationViewModel.changeHomeCurrentRoute(Route.Home.PlaylistItem.createRoute(it)) }
                )
            }
        }
        item {
            PlaylistSection(
                title = stringResource(id = R.string.Recommended_playlist_text),
                playlistState = recommendedPlaylistState
            ) { playlist ->
                RegularPlaylistItem(
                    playlistData = playlist,
                    onClick = { navigationViewModel.changeHomeCurrentRoute(Route.Home.PlaylistItem.createRoute(it)) }
                )
            }
        }
        item {
            PlaylistSection(
                title = stringResource(id = R.string.Type_based_playlist_text),
                playlistState = typedPlaylistState
            ) { playlist ->
                RegularPlaylistItem(
                    playlistData = playlist,
                    onClick = { navigationViewModel.changeHomeCurrentRoute(Route.Home.PlaylistItem.createRoute(it)) }
                )
            }
        }
    }
}

@Composable
fun PlaylistSection(
    title: String,
    playlistState: UiState<List<NewPipePlaylistData>>,
    itemContent: @Composable (NewPipePlaylistData) -> Unit
) {
    Column{
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        when (playlistState) {
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
                    message = (playlistState).message,
                    onRefresh = {}
                )
            }
            is UiState.Success -> {
                if (playlistState.data.isEmpty()) {
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
                            count = playlistState.data.size,
                            key = { index -> playlistState.data[index].id }
                        ) { index ->
                            itemContent(playlistState.data[index])
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
