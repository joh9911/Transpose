package com.example.transpose.media

import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS
import androidx.media3.datasource.DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.transpose.R
import com.example.transpose.media.audio_effect.AudioEffectHandlerImpl
import com.example.transpose.utils.constants.MediaSessionCallback
import com.google.common.util.concurrent.ListenableFuture
import javax.inject.Inject

@UnstableApi
class CustomMediaSessionCallback @Inject constructor(
    private val context: Context,
    private val audioEffectHandlerImpl: AudioEffectHandlerImpl
) : MediaSession.Callback {

    private fun createMyCustomCommands(): List<SessionCommand> {

        val myCommands = arrayListOf<SessionCommand>()

        val pitchCommand = SessionCommand(MediaSessionCallback.SET_PITCH, Bundle())

        val tempoCommand = SessionCommand(MediaSessionCallback.SET_TEMPO, Bundle())

        val bassBoostCommand = SessionCommand(MediaSessionCallback.SET_BASS_BOOST, Bundle())

        val loudnessEnhancementCommand =
            SessionCommand(MediaSessionCallback.SET_LOUDNESS_ENHANCER, Bundle())

        val equalizerPresetCommand =
            SessionCommand(MediaSessionCallback.SET_EQUALIZER_PRESET, Bundle())

        val equalizerCustomCommand =
            SessionCommand(MediaSessionCallback.SET_EQUALIZER_CUSTOM, Bundle())

        val disableEqualizerCommand = SessionCommand(MediaSessionCallback.DISABLE_EQUALIZER,Bundle())

        val reverbCommand = SessionCommand(MediaSessionCallback.SET_REVERB, Bundle())

        val disableReverbCommand = SessionCommand(MediaSessionCallback.DISABLE_REVERB, Bundle())

        val virtualizerCommand = SessionCommand(MediaSessionCallback.SET_VIRTUALIZER, Bundle())

        val environmentalReverbCommand =
            SessionCommand(MediaSessionCallback.SET_ENVIRONMENT_REVERB, Bundle())

        val pitchPlusCommand = SessionCommand(MediaSessionCallback.PITCH_PLUS, Bundle())

        val pitchMinusCommand = SessionCommand(MediaSessionCallback.PITCH_MINUS, Bundle())

        val tempoPlusCommand = SessionCommand(MediaSessionCallback.TEMPO_PLUS, Bundle())

        val tempMinusCommand = SessionCommand(MediaSessionCallback.TEMPO_MINUS, Bundle())

        val setHapticGenerator = SessionCommand(MediaSessionCallback.SET_HAPTIC_GENERATOR, Bundle())


        myCommands.add(pitchCommand)
        myCommands.add(tempoCommand)
        myCommands.add(bassBoostCommand)
        myCommands.add(loudnessEnhancementCommand)
        myCommands.add(equalizerPresetCommand)
        myCommands.add(equalizerCustomCommand)
        myCommands.add(reverbCommand)
        myCommands.add(virtualizerCommand)
        myCommands.add(environmentalReverbCommand)
        myCommands.add(pitchPlusCommand)
        myCommands.add(pitchMinusCommand)
        myCommands.add(tempoPlusCommand)
        myCommands.add(tempMinusCommand)
        myCommands.add(setHapticGenerator)
        return myCommands
    }

    private fun createCommandButton(): List<CommandButton> {
        val pitchMinusCommand = SessionCommand(MediaSessionCallback.PITCH_MINUS, Bundle())
        val pitchPlusCommand = SessionCommand(MediaSessionCallback.PITCH_PLUS, Bundle())

        val minusButton = CommandButton.Builder()
            .setSessionCommand(pitchMinusCommand)
            .setIconResId(R.drawable.baseline_exposure_neg_1_24)
            .setDisplayName("Minus")
            .build()


        val plusButton = CommandButton.Builder()
            .setSessionCommand(pitchPlusCommand)
            .setIconResId(R.drawable.baseline_exposure_plus_1_24)
            .setDisplayName("Plus")
            .build()


        return listOf(minusButton, plusButton)
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        super.onPostConnect(session, controller)
        session.setCustomLayout(controller, createCommandButton())
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(DEFAULT_CONNECT_TIMEOUT_MILLIS)
            .setReadTimeoutMs(DEFAULT_READ_TIMEOUT_MILLIS)
            .setAllowCrossProtocolRedirects(true)

//        val mediaSources = mediaItems.map {
//            val videoUri = it.mediaMetadata.extras?.getString("videoUrl")
//            val audioUri = it.mediaMetadata.extras?.getString("audioUrl")
//            if (videoUri != null && audioUri != null) {
//                Logger.d("$videoUri $audioUri")
//                val videoSource = createOptimizedSource(videoUri, dataSourceFactory)
//                val audioSource = createOptimizedSource(audioUri, dataSourceFactory)
//                MergingMediaSource(true, videoSource, audioSource)
//            } else {
//                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(it)
//            }
//        }

//        (mediaSession.player as? ExoPlayer)?.setMediaSources(mediaSources)

        return super.onAddMediaItems(mediaSession, controller, mediaItems)
    }

    private fun createOptimizedSource(uri: String, dataSourceFactory: DataSource.Factory): MediaSource {
        return when {
            uri.contains(".mpd") -> DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            uri.contains(".m3u8") -> HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            else -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
        }
    }




    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {

        val connectionResult = super.onConnect(session, controller)


        val commandButtons = createCommandButton()
        val myCustomCommands = createMyCustomCommands()
        val sessionCommands = connectionResult.availableSessionCommands.buildUpon()

        commandButtons.forEach { commandButton ->
            commandButton.sessionCommand?.let {
                sessionCommands.add(it)
            }
        }
        // Add custom commands
        myCustomCommands.forEach { customCommand ->
            sessionCommands.add(customCommand)
        }


        return MediaSession.ConnectionResult.accept(
            sessionCommands.build(), connectionResult.availablePlayerCommands
        )
    }


    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {

            MediaSessionCallback.UPDATE_METADATA -> {

            }

            MediaSessionCallback.SET_PITCH -> {
                val value =
                    customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setPitch(value)
            }

            MediaSessionCallback.SET_TEMPO -> {
                val value =
                    customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setTempo(value)
            }

            MediaSessionCallback.SET_BASS_BOOST -> {

                val value =
                    customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setBassBoost(value)
            }

            MediaSessionCallback.SET_LOUDNESS_ENHANCER -> {
                val value =
                    customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setLoudnessEnhancer(value)
            }

            MediaSessionCallback.SET_EQUALIZER_PRESET -> {
                val value = customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setEqualizerWithPreset(value)

            }

            MediaSessionCallback.SET_EQUALIZER_CUSTOM -> {
                val band = customCommand.customExtras.getInt("band")
                val level = customCommand.customExtras.getInt("level")
                audioEffectHandlerImpl.setEqualizerWithCustomValue(band, level)

            }

            MediaSessionCallback.DISABLE_EQUALIZER -> {
                audioEffectHandlerImpl.disableEqualizer()
            }

            MediaSessionCallback.SET_VIRTUALIZER -> {
                val value =
                    customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setVirtualizer(value)
            }

            MediaSessionCallback.SET_REVERB -> {
                val presetIndex =
                    customCommand.customExtras.getInt("presetIndex")
                val sendLevel =
                    customCommand.customExtras.getInt("sendLevel")
                audioEffectHandlerImpl.setPresetReverb(presetIndex, sendLevel)

            }

            MediaSessionCallback.DISABLE_REVERB -> {
                audioEffectHandlerImpl.disableReverb()
            }

            MediaSessionCallback.SET_ENVIRONMENT_REVERB -> {
                val isEnabled = customCommand.customExtras.getBoolean("isEnabled")
                val roomLevel = customCommand.customExtras.getInt("roomLevel")
                val roomHFLevel = customCommand.customExtras.getInt("roomHFLevel")
                val decayTime = customCommand.customExtras.getInt("decayTime")
                val decayHFRatio = customCommand.customExtras.getInt("decayHFRatio")
                val reflectionsLevel = customCommand.customExtras.getInt("reflectionsLevel")
                val reflectionsDelay = customCommand.customExtras.getInt("reflectionsDelay")
                val reverbLevel = customCommand.customExtras.getInt("reverbLevel")
                val reverbDelay = customCommand.customExtras.getInt("reverbDelay")
                val diffusion = customCommand.customExtras.getInt("diffusion")
                val density = customCommand.customExtras.getInt("density")

                audioEffectHandlerImpl.setEnvironmentalReverb(
                    isEnabled,
                    roomLevel,
                    roomHFLevel,
                    decayTime,
                    decayHFRatio,
                    reflectionsLevel,
                    reflectionsDelay,
                    reverbLevel,
                    reverbDelay,
                    diffusion,
                    density
                )
            }

            MediaSessionCallback.PITCH_MINUS -> {
                audioEffectHandlerImpl.pitchMinusOne()
            }

            MediaSessionCallback.PITCH_PLUS -> {
                audioEffectHandlerImpl.pitchPlusOne()
            }

            MediaSessionCallback.TEMPO_MINUS -> {
                audioEffectHandlerImpl.tempoMinusOne()
            }
            MediaSessionCallback.TEMPO_PLUS -> {
                audioEffectHandlerImpl.tempoPlusOne()
            }
            MediaSessionCallback.SET_HAPTIC_GENERATOR -> {
                val isEnabled =
                    customCommand.customExtras.getBoolean("isEnabled")
                audioEffectHandlerImpl.setHapticGenerator(isEnabled)
            }


        }
        return super.onCustomCommand(session, controller, customCommand, args)
    }
}