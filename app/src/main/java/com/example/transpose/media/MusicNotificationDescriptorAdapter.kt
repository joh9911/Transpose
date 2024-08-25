package com.example.transpose.media

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@UnstableApi
class MusicNotificationDescriptorAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.albumTitle ?: "Unknown"

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: "Unknown"

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        CoroutineScope(Dispatchers.IO).launch {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(player.mediaMetadata.artworkUri)
                .target { drawable ->
                    val bitmap = drawable.toBitmap()
                    callback.onBitmap(bitmap)
                }
                .build()

            imageLoader.enqueue(request)
        }
        return null
    }
}