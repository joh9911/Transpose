package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.R
import com.example.transpose.data.model.newpipe.NewPipeVideoData

@Composable
fun VideoInfoItem(currentVideoItem: NewPipeVideoData?) {
    val viewCountFormats = rememberStringArrayResource(R.array.view_count_formats)

    Column(modifier = Modifier
        .padding(top = 10.dp)
        .fillMaxWidth()) {
        Text(
            text = currentVideoItem?.title ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${currentVideoItem?.viewCountCalculator(viewCountStringArray = viewCountFormats, viewCountString = currentVideoItem.viewCount.toString())} • ${currentVideoItem?.textualUploadDate}",
            modifier = Modifier.padding(top = 5.dp, start = 10.dp)
        )
        // 채널 정보 등 추가
    }
}
@Composable
fun rememberStringArrayResource(resourceId: Int): Array<String> {
    val context = LocalContext.current
    return remember(resourceId) {
        context.resources.getStringArray(resourceId)
    }
}