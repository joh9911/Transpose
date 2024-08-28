package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transpose.data.model.newpipe.NewPipeVideoData

@Composable
fun VideoInfoItem(currentVideoItem: NewPipeVideoData?) {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(text = currentVideoItem?.title ?: "")
        Text(text = "${currentVideoItem?.viewCount} views • ${currentVideoItem?.textualUploadDate}")
        // 채널 정보 등 추가
    }
}
