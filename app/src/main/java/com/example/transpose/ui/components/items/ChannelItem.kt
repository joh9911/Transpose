package com.example.transpose.ui.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.transpose.R
import com.example.transpose.data.model.newpipe.NewPipeChannelData
import com.example.transpose.ui.components.bottomsheet.item.rememberStringArrayResource
import com.example.transpose.utils.TextFormatUtil
import com.example.transpose.utils.ToastUtil

@Composable
fun ChannelItem(
    channel: NewPipeChannelData,
    onClick: (NewPipeChannelData) -> Unit
) {
    val subscriberCountFormats = rememberStringArrayResource(R.array.subscriber_count_formats)
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { ToastUtil.showNotImplemented(context) }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Avatar
            Spacer(modifier = Modifier.width(20.dp))
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(channel.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Channel Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(70.dp))

            // Channel Info
            Column {
                Text(
                    text = channel.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "@${channel.infoType}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "구독자 ${
                        TextFormatUtil.subscriberCountConverter(
                            channel.subscriberCount.toString(),
                            subscriberCountFormats
                        )
                    }명",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

