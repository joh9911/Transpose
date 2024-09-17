package com.example.transpose.ui.screen.library.my_playlist.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.transpose.R
import com.example.transpose.ui.components.dropdown_menu.DropDownMenu

@Composable
fun PlaylistItem(
    title: String,
    onClick: () -> Unit,
    dropDownMenuClick: () -> Unit
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.clickable(onClick = onClick),) {
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 10.dp)
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_library_music_24_blue),
                contentDescription = "Playlist icon",
                modifier = Modifier.size(70.dp)
            )
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box{
                IconButton(onClick = {isExpanded = true}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                DropDownMenu(text = stringResource(id = R.string.my_playlist_pop_up_menu_delete_playlist_text),isExpanded = isExpanded, onDismissRequest = {isExpanded = false}, onClick = { dropDownMenuClick()})
            }

        }
        Spacer(modifier = Modifier.size(10.dp))

    }

}