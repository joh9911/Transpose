package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.utils.constants.AppColors

@Composable
fun ChannelSection(
    currentVideoItem: NewPipeVideoData?,
    mediaViewModel: MediaViewModel,
    mainViewModel: MainViewModel
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { mainViewModel.fetchChannelData(currentVideoItem!!) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = currentVideoItem?.uploaderAvatars?.first()?.url,
            contentDescription = "Video Thumbnail",
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .padding(10.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = currentVideoItem?.uploaderName ?: "",
            modifier = Modifier
                .weight(1f)
                .width(150.dp)
                .padding(end = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, AppColors.BeforeGettingDataColor),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .padding(end = 10.dp)
                .width(60.dp)
                .height(30.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_button_text),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

    }
}



