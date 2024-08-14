package com.example.transpose.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transpose.data.model.NewPipeContentListData

@Composable
fun PlaylistItem(item: NewPipeContentListData) {
    // Implement your playlist item UI here
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(end = 16.dp)
    ) {
        // Playlist item content
    }
}