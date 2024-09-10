package com.example.transpose.ui.screen.library.my_playlist

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.Route
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.screen.library.my_playlist.items.AddPlaylistItem
import com.example.transpose.ui.screen.library.my_playlist.items.AudioStorageItem
import com.example.transpose.ui.screen.library.my_playlist.items.PlaylistItem
import com.example.transpose.ui.screen.library.my_playlist.items.VideoStorageItem
import com.example.transpose.utils.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryMyPlaylistScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    libraryMyPlaylistViewModel: LibraryMyPlaylistViewModel
) {

    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()
    val myPlaylists by libraryMyPlaylistViewModel.myPlaylists.collectAsState()

    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var pendingRoute by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            pendingRoute?.let { route ->
                navigationViewModel.changeLibraryCurrentRoute(route)
                pendingRoute = null
            }
        } else {
            if (PermissionUtils.shouldShowRationale(context)) {
                showRationaleDialog = true
            } else {
                showSettingsDialog = true
            }
        }
    }

    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }


    fun handleItemClick(route: String) {
        if (PermissionUtils.checkPermissions(context)) {
            navigationViewModel.changeLibraryCurrentRoute(route)
        } else {
            pendingRoute = route
            PermissionUtils.requestPermissions(permissionLauncher::launch)
        }
    }


    var showPlaylistDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }


    LazyColumn() {
        item {
            AddPlaylistItem(onClick = { showPlaylistDialog = true })

        }

        item {
            AudioStorageItem(onClick = {
                handleItemClick(Route.Library.MyLocalFileItem.createRoute("audio"))
            })

        }
        item {
            VideoStorageItem(onClick = {
                handleItemClick(Route.Library.MyLocalFileItem.createRoute("video"))

            })

        }
        items(myPlaylists.size) { index ->
            val item = myPlaylists[index]
            PlaylistItem(
                title = item.name,
                onClick = { navigationViewModel.changeLibraryCurrentRoute(Route.Library.MyPlaylistItem.createRoute(item.playlistId.toString())) },
                dropDownMenuClick = {libraryMyPlaylistViewModel.deleteMyPlaylist(item)}
            )
        }
    }

    if (showPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showPlaylistDialog = false },
            title = { Text("create playlist") },
            text = {
                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("플레이리스트 이름") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            libraryMyPlaylistViewModel.createMyPlaylist(playlistName)
                            showPlaylistDialog = false
                            playlistName = ""
                        }
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPlaylistDialog = false
                        playlistName = ""
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("권한 필요") },
            text = { Text("이 기능을 사용하려면 저장소 접근 권한이 필요합니다.") },
            confirmButton = {
                Button(onClick = {
                    showRationaleDialog = false
                    PermissionUtils.requestPermissions(permissionLauncher::launch)
                }) {
                    Text("권한 요청")
                }
            },
            dismissButton = {
                Button(onClick = { showRationaleDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("권한 필요") },
            text = { Text("설정에서 저장소 접근 권한을 허용해주세요.") },
            confirmButton = {
                Button(onClick = {
                    showSettingsDialog = false
                    PermissionUtils.openAppSettings(context)
                }) {
                    Text("설정으로 이동")
                }
            },
            dismissButton = {
                Button(onClick = { showSettingsDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

}


