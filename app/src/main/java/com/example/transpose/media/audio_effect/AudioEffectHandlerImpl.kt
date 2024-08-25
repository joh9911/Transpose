package com.example.transpose.media.audio_effect

import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.EnvironmentalReverb
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.transpose.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log2
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
        Logger.d("setPitch $value")
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

    override fun setEqualizerWithPreset(value: Int) {
        if (equalizer == null){
            try{
                equalizer = Equalizer(0, exoPlayer.audioSessionId)
                equalizer?.enabled = true
            }
            catch (e: Exception){
            }
        }
        for (i in 0 until equalizer!!.numberOfPresets) {
            Logger.d("이퀼라이저 ${equalizer!!.getPresetName(i.toShort())}")
        }

        equalizer?.usePreset(value.toShort())
    }


    override fun setEqualizerWithCustomValue(changedBand: Int, newGainLevel: Int) {
        if (equalizer == null){
            try{
                equalizer = Equalizer(0, exoPlayer.audioSessionId)
                equalizer?.enabled = true
            }
            catch (e: Exception){
            }
        }

        equalizer?.setBandLevel(changedBand.toShort(), newGainLevel.toShort())
    }

    private fun disableEqualizer(){
        setEqualizerWithPreset(3)
        equalizer?.release()
        equalizer = null
    }

    override fun setVirtualizer(value: Int) {
        if (virtualizer == null){
            virtualizer = Virtualizer(0, audioSessionId)
            virtualizer?.enabled = true
            virtualizer?.forceVirtualizationMode(Virtualizer.VIRTUALIZATION_MODE_BINAURAL)
        }

        virtualizer?.setStrength(value.toShort())
        virtualizer?.enabled = true

        exoPlayer.setAuxEffectInfo(AuxEffectInfo(virtualizer!!.id, 0.5f))
    }

    override fun setPresetReverb(presetIndex: Int, sendLevel: Int) {
        if (presetReverb == null) {
            try {
                presetReverb = PresetReverb(1, audioSessionId)
                presetReverb?.enabled = true
            } catch (e: Exception) {
                return
            }
        }

        try {
            presetReverb?.preset = presetIndex.toShort()

            val auxEffectSendLevel = sendLevel.toFloat()

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

    override fun pitchPlusOne() {
        val currentPitch = exoPlayer.playbackParameters.pitch
        val currentTempoValue = exoPlayer.playbackParameters.speed

        val currentSemitones = 12 * log2(currentPitch.toDouble())

        val newSemitones = currentSemitones + 1

        val newPitch = 2.0.pow(newSemitones / 12.0).toFloat()

        exoPlayer.playbackParameters = PlaybackParameters(currentTempoValue, newPitch)
    }

    override fun pitchMinusOne() {
        val currentPitch = exoPlayer.playbackParameters.pitch
        val currentTempoValue = exoPlayer.playbackParameters.speed

        val currentSemitones = 12 * log2(currentPitch.toDouble())

        val newSemitones = currentSemitones - 1

        val newPitch = 2.0.pow(newSemitones / 12.0).toFloat()

        exoPlayer.playbackParameters = PlaybackParameters(currentTempoValue, newPitch)    }

    override fun tempoPlusOne() {
        val currentPitch = exoPlayer.playbackParameters.pitch
        val currentTempoValue = exoPlayer.playbackParameters.speed

        val currentSemitones = 12 * log2(currentTempoValue.toDouble())

        val newSemitones = currentSemitones + 1

        val newTempo = 2.0.pow(newSemitones / 12.0).toFloat()

        exoPlayer.playbackParameters = PlaybackParameters(newTempo, currentPitch)    }

    override fun tempoMinusOne() {
        val currentPitch = exoPlayer.playbackParameters.pitch
        val currentTempoValue = exoPlayer.playbackParameters.speed

        val currentSemitones = 12 * log2(currentTempoValue.toDouble())

        val newSemitones = currentSemitones - 1

        val newTempo = 2.0.pow(newSemitones / 12.0).toFloat()

        exoPlayer.playbackParameters = PlaybackParameters(newTempo, currentPitch)    }
}