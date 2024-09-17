package com.example.transpose.ui.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.transpose.R
import com.example.transpose.data.database.entity.PlaylistEntity
import com.example.transpose.ui.screen.library.my_playlist.items.PlaylistItem

@Composable
fun AddVideoToPlaylistDialog(
    playlists: List<PlaylistEntity>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.video_pop_up_menu_add_playlist_text)) },
        text = {
            LazyColumn {
                items(playlists.size) { index ->
                    val item = playlists[index]
                    PlaylistItem(
                        title = item.name,
                        onClick = { onPlaylistSelected(item.playlistId)
                                  onDismiss()},
                        dropDownMenuClick = {})

                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.dialog_cancel_text))
            }
        }
    )
}