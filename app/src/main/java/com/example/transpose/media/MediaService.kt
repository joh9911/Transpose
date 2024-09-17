package com.example.transpose.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.transpose.MainActivity
import com.example.transpose.media.audio_effect.AudioEffectHandlerImpl
import com.example.transpose.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var audioEffectHandlerImpl: AudioEffectHandlerImpl


    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val customCallback = CustomMediaSessionCallback(this, audioEffectHandlerImpl)
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(customCallback)
            .build()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        mediaSession?.setSessionActivity(pendingIntent)

        createNotificationChannel()
    }



    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "2",
                "Music Player Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            serviceChannel.setSound(null,null)
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }



    override fun onTaskRemoved(rootIntent: Intent?) {
        Logger.d("onTaskRemoved")
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        if (!exoPlayer.playWhenReady
            || exoPlayer.mediaItemCount == 0
            || exoPlayer.playbackState == Player.STATE_ENDED
        ) {

            stopSelf()
        }
    }


    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession


}