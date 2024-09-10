package com.example.transpose.ui.screen.library.my_playlist_item.items

import android.provider.MediaStore.Video
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.transpose.data.database.entity.VideoEntity
import com.example.transpose.ui.components.dropdown_menu.DropDownMenu

@Composable
fun PlaylistVideoItem(
    item: VideoEntity,
    onClick: (VideoEntity) -> Unit,
    dropDownMenuClick: () -> Unit
){
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick(item) }
            .padding(vertical = 10.dp, horizontal = 10.dp)
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = "Thumbnail",
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray)

        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = item.description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Box{
                IconButton(onClick = {isExpanded = true}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                DropDownMenu(text = "delete",isExpanded = isExpanded, onDismissRequest = {isExpanded = false}, onClick = { dropDownMenuClick()})
            }
        }


    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}