package com.example.transpose.service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.transpose.R
import com.example.transpose.service.audio_effect.AudioEffectHandlerImpl
import com.example.transpose.utils.constants.MediaSessionCallback
import com.google.common.util.concurrent.ListenableFuture
import javax.inject.Inject

@UnstableApi
class CustomMediaSessionCallback @Inject constructor(
    private val audioEffectHandlerImpl: AudioEffectHandlerImpl
) : MediaSession.Callback {

    private fun createMyCustomCommands(): List<SessionCommand> {

        val myCommands = arrayListOf<SessionCommand>()

        val bassBoostCommand = SessionCommand(MediaSessionCallback.SET_BASS_BOOST, Bundle())

        val loudnessEnhancementCommand =
            SessionCommand(MediaSessionCallback.SET_LOUDNESS_ENHANCER, Bundle())

        val equalizerCommand = SessionCommand(MediaSessionCallback.SET_EQUALIZER, Bundle())

        val reverbCommand = SessionCommand(MediaSessionCallback.SET_REVERB, Bundle())

        val virtualizerCommand = SessionCommand(MediaSessionCallback.SET_VIRTUALIZER, Bundle())

        val environmentalReverbCommand =
            SessionCommand(MediaSessionCallback.SET_ENVIRONMENT_REVERB, Bundle())

        myCommands.add(bassBoostCommand)
        myCommands.add(loudnessEnhancementCommand)
        myCommands.add(equalizerCommand)
        myCommands.add(reverbCommand)
        myCommands.add(virtualizerCommand)
        myCommands.add(environmentalReverbCommand)
        return myCommands
    }

    private fun createCommandButton(): List<CommandButton> {
        val pitchMinusCommand = SessionCommand(MediaSessionCallback.MINUS, Bundle())
        val pitchPlusCommand = SessionCommand(MediaSessionCallback.PLUS, Bundle())

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

            MediaSessionCallback.SET_EQUALIZER -> {
                val value = customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setEqualizer(value)

            }

            MediaSessionCallback.SET_VIRTUALIZER -> {
                val value =
                    customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setVirtualizer(value)
            }

            MediaSessionCallback.SET_REVERB -> {
                val value =
                    customCommand.customExtras.getInt("value")
                val sendLevel =
                    customCommand.customExtras.getInt("sendLevel")
                audioEffectHandlerImpl.setPresetReverb(value, sendLevel)

            }

            MediaSessionCallback.SET_ENVIRONMENT_REVERB -> {

            }

            MediaSessionCallback.MINUS -> {
                val value = customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setPitch(value)
            }

            MediaSessionCallback.PLUS -> {
                val value = customCommand.customExtras.getInt("value")
                audioEffectHandlerImpl.setPitch(value)
            }

        }
        return super.onCustomCommand(session, controller, customCommand, args)
    }
}