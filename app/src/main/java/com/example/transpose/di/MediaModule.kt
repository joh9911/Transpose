package com.example.transpose.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.example.transpose.Application
import com.example.transpose.data.repository.suggestionkeyword.SuggestionKeywordRepository
import com.example.transpose.data.repository.suggestionkeyword.SuggestionKeywordRepositoryImpl
import com.example.transpose.service.MusicNotificationManager
import com.example.transpose.service.MusicServiceHandler
import com.example.transpose.service.audio_effect.AudioEffectHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaModule {

    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()


    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .build()

    @Provides
    @Singleton
    fun providePlayer(exoPlayer: ExoPlayer): Player = exoPlayer


//    @Provides
//    @Singleton
//    fun provideMediaSession(
//        @ApplicationContext context: Context,
//        player: Player
//    ): MediaSession = MediaSession.Builder(context, player).build()


//    @Provides
//    @Singleton
//    fun provideMusicNotificationManager(
//        @ApplicationContext context: Context,
//        player: Player
//    ): MusicNotificationManager = MusicNotificationManager(context, player as ExoPlayer)
//
//    @Provides
//    @Singleton
//    fun provideMusicServiceHandler(player: Player): MusicServiceHandler = MusicServiceHandler(player as ExoPlayer)

    @Provides
    @Singleton
    fun provideAudioEffectHandler(player: Player): AudioEffectHandlerImpl =
        AudioEffectHandlerImpl(player as ExoPlayer)

}