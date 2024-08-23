package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VideoInfoItem(videoTitle: String) {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(text = videoTitle)
        Text(text = "1,000,000 views • 2 days ago")
        // 채널 정보 등 추가
    }
}
