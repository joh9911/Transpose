package com.example.transpose.ui.components.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView

@Composable
fun Player() {
    val context = LocalContext.current
//    AndroidView(
//        factory = { ctx ->
//            PlayerView(ctx).apply {
//                player = exoPlayer
//            }
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp) // Set your desired height
//    )
}