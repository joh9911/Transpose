package com.example.transpose.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.transpose.R


class MusicNotificationManager(
    private val context: Context,
    private val exoPlayer: Player
) {

    private val musicNotificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createMusicNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMusicNotificationChannel() {
        val musicNotificationChannel = NotificationChannel(
            "id",
            "name",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        musicNotificationManager.createNotificationChannel(musicNotificationChannel)
    }

    @UnstableApi
    private fun buildMusicNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(
            context,
            2,
            "1"
        )
            .setMediaDescriptionAdapter(
                MusicNotificationDescriptorAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )
            .setSmallIconResourceId(R.drawable.outline_library_music_24)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.platformToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(true)
                it.setUsePreviousActionInCompactView(true)
                it.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                it.setPlayer(exoPlayer)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @UnstableApi
    fun startMusicNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildMusicNotification(mediaSession)
        startForegroundMusicService(mediaSessionService)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundMusicService(mediaSessionService: MediaSessionService) {
        val musicNotification = Notification.Builder(context, "0")
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        mediaSessionService.startForeground(2, musicNotification)
    }
}