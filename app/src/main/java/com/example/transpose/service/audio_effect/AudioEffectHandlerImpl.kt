package com.example.transpose.service.audio_effect

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.EnvironmentalReverb
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.SessionCommand
import com.example.transpose.utils.constants.MediaSessionCallback
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@OptIn(UnstableApi::class)
@Singleton
class AudioEffectHandlerImpl @Inject constructor(
    private val exoPlayer: ExoPlayer
):AudioEffectHandler {

    private val audioSessionId: Int
        get() = exoPlayer.audioSessionId

    private var equalizer: Equalizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var presetReverb: PresetReverb? = null
    private var environmentalReverb: EnvironmentalReverb? = null
    private var dynamicsProcessing: DynamicsProcessing? = null

    override fun setPitch(value: Int) {
        val semitonesFromCenter = (value - 100) * 0.1
        val adjustedPitch = 2.0.pow(semitonesFromCenter / 12.0).toFloat()
        val currentTempoValue = exoPlayer.playbackParameters.speed
        exoPlayer.playbackParameters = PlaybackParameters(currentTempoValue, adjustedPitch)
    }

    override fun setTempo(value: Int) {
        val semitonesFromCenter = (value - 100) * 0.1
        val adjustedTempo = 2.0.pow(semitonesFromCenter / 12.0).toFloat()
        val currentPitchValue = exoPlayer.playbackParameters.pitch
        exoPlayer.playbackParameters = PlaybackParameters(adjustedTempo, currentPitchValue)
    }


    override fun setBassBoost(value: Int) {
        if (bassBoost == null){
            try{
                bassBoost = BassBoost(0, audioSessionId)
                bassBoost?.enabled = true
            }
            catch (e: Exception){
            }
        }

        if (audioSessionId != AudioEffect.ERROR_BAD_VALUE) {
            bassBoost?.let {
                if (it.strengthSupported) {
                    it.setStrength(value.toShort())
                    exoPlayer.setAuxEffectInfo(AuxEffectInfo(it.id, 1f))
                }
            }

        }
    }

    private fun initAudioEffect(){
        try{
            equalizer = Equalizer(0, exoPlayer.audioSessionId)
            equalizer?.enabled = true

            bassBoost = BassBoost(0, audioSessionId)
            bassBoost?.enabled = true

            loudnessEnhancer = LoudnessEnhancer(audioSessionId)
            loudnessEnhancer?.enabled = true

            virtualizer = Virtualizer(0, audioSessionId)
            virtualizer?.enabled = true
            virtualizer?.forceVirtualizationMode(Virtualizer.VIRTUALIZATION_MODE_BINAURAL)

            presetReverb = PresetReverb(1, 0)
            presetReverb?.enabled = true
        }
        catch (e: Exception){
        }

    }

    override fun setLoudnessEnhancer(value: Int) {
        if (loudnessEnhancer == null){
            loudnessEnhancer = LoudnessEnhancer(audioSessionId)
            loudnessEnhancer?.enabled = true
        }

        if (audioSessionId != AudioEffect.ERROR_BAD_VALUE){
            loudnessEnhancer?.setTargetGain(value)
        }
    }

    override fun setEqualizer(value: Int?) {
        value ?: return
        if (value == -1){
            disableEqualizer()
            return
        }
        if (equalizer == null){
            try{
                equalizer = Equalizer(0, exoPlayer.audioSessionId)
                equalizer?.enabled = true
            }
            catch (e: Exception){
            }
        }

        equalizer?.usePreset(value.toShort())

        val i = equalizer?.numberOfBands!!
//        val intent = Intent(Actions.GET_EQUALIZER_INFO)
//        for (index in 0 until i){
//            Log.d("이퀼라이저","${equalizer?.getBandLevel(index.toShort())}")
//
//            intent.putExtra("$index","${equalizer?.getBandLevel(index.toShort())}")
//
//        }
//        sendBroadcast(intent)
    }

    private fun disableEqualizer(){
        setEqualizer(3)
        equalizer?.release()
        equalizer = null
    }

    override fun setVirtualizer(value: Int) {
        if (virtualizer == null){
            virtualizer = Virtualizer(0, audioSessionId)
            virtualizer?.enabled = true
            virtualizer?.forceVirtualizationMode(Virtualizer.VIRTUALIZATION_MODE_BINAURAL)
        }
    }

    override fun setPresetReverb(value: Int, sendLevel: Int) {
        if (value == -1 && sendLevel == -1) {
            disablePresetReverb()
            return
        }

        if (presetReverb == null) {
            try {
                presetReverb = PresetReverb(1, audioSessionId)
                presetReverb?.enabled = true
            } catch (e: Exception) {
                return
            }
        }

        try {
            presetReverb?.preset = value.toShort()

            val auxEffectSendLevel = if (sendLevel == -1) {
                1f
            } else {
                sendLevel.toFloat() / 100f
            }

            exoPlayer.setAuxEffectInfo(AuxEffectInfo(presetReverb!!.id, auxEffectSendLevel))
        } catch (e: Exception) {
            Log.d("예외", "프리셋 리버브 설정 오류: $e")
        }
    }

    private fun disablePresetReverb() {
        presetReverb?.enabled = false
        exoPlayer.setAuxEffectInfo(AuxEffectInfo(0, 0f))  // 효과 비활성화
        presetReverb?.release()
        presetReverb = null
    }

    override fun setEnvironmentalReverb() {

    }
}