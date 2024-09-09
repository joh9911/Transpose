package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.ui.common.PlayableItemUiState
import com.example.transpose.utils.TextFormatUtil
import com.valentinilk.shimmer.shimmer

@Composable
fun VideoInfoSection(mediaViewModel: MediaViewModel) {
    val viewCountFormats = rememberStringArrayResource(R.array.view_count_formats)
    val currentVideoItemState by mediaViewModel.currentVideoItemState.collectAsState()

    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        when (currentVideoItemState) {
            is PlayableItemUiState.Initial -> {
                FullShimmerEffect()
            }

            is PlayableItemUiState.BasicInfoLoaded -> {
                val basicInfo =
                    (currentVideoItemState as PlayableItemUiState.BasicInfoLoaded).basicInfo
                Text(
                    text = basicInfo.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                PartialShimmerEffect()
            }

            is PlayableItemUiState.FullInfoLoaded -> {
                val fullInfo =
                    (currentVideoItemState as PlayableItemUiState.FullInfoLoaded).fullInfo
                Text(
                    text = fullInfo.title,
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
                    text = "${
                        TextFormatUtil.viewCountCalculator(
                            viewCountStringArray = viewCountFormats,
                            viewCountString = fullInfo.viewCount.toString()
                        )

                    } • ${fullInfo.textualUploadDate}",
                    modifier = Modifier.padding(top = 5.dp, start = 10.dp)
                )
            }

            is PlayableItemUiState.Error -> {
                Text(
                    text = "Error: ${(currentVideoItemState as PlayableItemUiState.Error).message}",
                    color = Color.Red,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun FullShimmerEffect() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shimmer()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(start = 10.dp, end = 10.dp)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .padding(start = 10.dp)
                .background(Color.LightGray)
        )
    }
}

@Composable
fun PartialShimmerEffect() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(16.dp)
            .padding(start = 10.dp, top = 5.dp)
            .shimmer()
            .background(Color.LightGray)
    )
}

@Composable
fun rememberStringArrayResource(resourceId: Int): Array<String> {
    val context = LocalContext.current
    return remember(resourceId) {
        context.resources.getStringArray(resourceId)
    }
}