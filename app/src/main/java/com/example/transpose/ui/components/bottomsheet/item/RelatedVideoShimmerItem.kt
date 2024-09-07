package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer


@Composable
fun RelatedVideoShimmerItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .shimmer()
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp)
        ) {
            // Title placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Info type placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(10.dp)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(5.dp))
            // Additional info placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(10.dp)
                    .background(Color.LightGray)
            )
        }

        // More options icon placeholder
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.LightGray)
        )
    }
}