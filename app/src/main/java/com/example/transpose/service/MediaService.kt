package com.example.transpose.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaSession2Service
import android.media.browse.MediaBrowser
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.transpose.MainActivity
import com.example.transpose.service.audio_effect.AudioEffectHandlerImpl
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
        Logger.d("Service 생성!")
        val customCallback = CustomMediaSessionCallback(audioEffectHandlerImpl)
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
            Log.d("채널 ","생성")
            val serviceChannel = NotificationChannel(
                "2",
                "Music Player Channel", // 채널표시명
                NotificationManager.IMPORTANCE_HIGH
            )
            serviceChannel.setSound(null,null)
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }



    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {

        if (!exoPlayer.playWhenReady
            || exoPlayer.mediaItemCount == 0
            || exoPlayer.playbackState == Player.STATE_ENDED
        ) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    // Remember to release the player and media session in onDestroy
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