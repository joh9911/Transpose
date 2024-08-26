package com.example.transpose.ui.screen.library.my_playlist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.transpose.R

@Composable
fun AudioStorageItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 10.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_music_video_24),
            contentDescription = "Music storage icon",
            modifier = Modifier.size(70.dp)
        )
        Text(
            text = "Search My Music",
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}