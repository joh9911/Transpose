package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RelatedVideoItem(index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp, 80.dp)
                .background(Color.Gray)
        )

        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text("Related Video $index" )
            Text("Channel Name", )
            Text("1M views • 2 days ago")
        }
    }
}