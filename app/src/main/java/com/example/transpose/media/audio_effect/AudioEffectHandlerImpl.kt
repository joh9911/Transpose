package com.example.transpose.media.audio_effect

import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.EnvironmentalReverb
import android.media.audiofx.Equalizer
import android.media.audiofx.HapticGenerator
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.os.Build
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
) : AudioEffectHandler {

    private val audioSessionId: Int
        get() = exoPlayer.audioSessionId

    private var equalizer: Equalizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var presetReverb: PresetReverb? = null
    private var environmentalReverb: EnvironmentalReverb? = null
    private var dynamicsProcessing: DynamicsProcessing? = null
    private var hapticGenerator: HapticGenerator? = null


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
        try {
            if (value == 0) {
                disableBassBoost()
                return
            }

            if (bassBoost == null) {
                bassBoost = BassBoost(0, audioSessionId)
                bassBoost?.enabled = true
                Logger.d("if (bassBoost == null)")
            }

            if (audioSessionId != AudioEffect.ERROR_BAD_VALUE) {
                bassBoost?.let {
                    if (it.strengthSupported) {
                        it.setStrength(value.toShort())
                        it.enabled = true
                        exoPlayer.setAuxEffectInfo(AuxEffectInfo(it.id, 1f))
                        Logger.d("exoPlayer.setAuxEffectInfo(AuxEffectInfo(it.id, 1f))")
                    }
                }

            }

        } catch (e: Exception) {
            Logger.d("setBassBoost $e")
        }


    }

    override fun disableBassBoost() {
        bassBoost?.release()
        bassBoost = null
    }


    override fun setLoudnessEnhancer(value: Int) {
        try {
            if (value == 0) {
                disableLoudnessEnhancer()
                return
            }

            if (loudnessEnhancer == null) {
                loudnessEnhancer = LoudnessEnhancer(audioSessionId)
                loudnessEnhancer?.enabled = true
            }

            if (audioSessionId != AudioEffect.ERROR_BAD_VALUE) {
                loudnessEnhancer?.setTargetGain(value)
            }
        } catch (e: Exception) {
            Logger.d("setLoudnessEnhancer $e")
        }

    }

    override fun disableLoudnessEnhancer() {
        loudnessEnhancer?.release()
        loudnessEnhancer = null
    }

    override fun setEqualizerWithPreset(value: Int) {
        try {
            if (equalizer == null) {
                equalizer = Equalizer(0, exoPlayer.audioSessionId)
                equalizer?.enabled = true
            }

            equalizer?.usePreset(value.toShort())
            equalizer?.enabled = true

        } catch (e: Exception) {
            Logger.d("setEqualizerWithPreset $e")

        }
    }


    override fun setEqualizerWithCustomValue(changedBand: Int, newGainLevel: Int) {
        try {
            if (equalizer == null) {
                equalizer = Equalizer(0, exoPlayer.audioSessionId)
                equalizer?.enabled = true
            }
            equalizer?.setBandLevel(changedBand.toShort(), newGainLevel.toShort())
            equalizer?.enabled = true


        } catch (e: Exception) {
            Logger.d("setEqualizerWithCustomValue $e")

        }
    }


    override fun disableEqualizer() {
        equalizer?.release()
        equalizer = null
    }


    override fun setVirtualizer(value: Int) {

        try {
            if (value == 0) {
                virtualizer?.release()
                virtualizer = null
                return
            }
            if (virtualizer == null) {
                virtualizer = Virtualizer(0, audioSessionId)
                virtualizer?.enabled = true
                virtualizer?.forceVirtualizationMode(Virtualizer.VIRTUALIZATION_MODE_BINAURAL)
            }

            virtualizer?.setStrength(value.toShort())
            virtualizer?.enabled = true
            virtualizer?.forceVirtualizationMode(Virtualizer.VIRTUALIZATION_MODE_BINAURAL)

            exoPlayer.setAuxEffectInfo(AuxEffectInfo(virtualizer!!.id, 0.5f))
        } catch (e: Exception) {
            Logger.d("setVirtualizer $virtualizer")
        }

    }

    override fun disableVirtualizer() {
        virtualizer?.release()
        virtualizer = null
    }

    override fun setPresetReverb(presetIndex: Int, sendLevel: Int) {
        try {
            if (presetReverb == null) {
                presetReverb = PresetReverb(1, 0)
            }

            presetReverb?.preset = presetIndex.toShort()
            presetReverb?.enabled = true

            val auxEffectSendLevel = sendLevel.toFloat()
            exoPlayer.setAuxEffectInfo(AuxEffectInfo(presetReverb!!.id, auxEffectSendLevel))
        }catch (e: Exception){
            Logger.d("setPresetReverb $e")
        }
    }

    override fun disableReverb() {
        presetReverb?.release()
        presetReverb = null
    }


    override fun setEnvironmentalReverb(
        isEnabled: Boolean,
        roomLevel: Int,
        roomHFLevel: Int,
        decayTime: Int,
        decayHFRatio: Int,
        reflectionsLevel: Int,
        reflectionsDelay: Int,
        reverbLevel: Int,
        reverbDelay: Int,
        diffusion: Int,
        density: Int
    ) {

        try {
            if (!isEnabled) {
                disableEnvironmentReverb()

                return
            }

            if (environmentalReverb == null) {
                environmentalReverb = EnvironmentalReverb(0, 0)
                environmentalReverb?.enabled = true
            }

            environmentalReverb?.apply {
                setRoomLevel(roomLevel.toShort())
                setRoomHFLevel(roomHFLevel.toShort())
                setDecayTime(decayTime)
                setDecayHFRatio(decayHFRatio.toShort())
                setReflectionsLevel(reflectionsLevel.toShort())
                setReflectionsDelay(reflectionsDelay)
                setReverbLevel(reverbLevel.toShort())
                setReverbDelay(reverbDelay)
                setDiffusion(diffusion.toShort())
                setDensity(density.toShort())
            }
            Logger.d("setEnvironmentalReverb ${EnvironmentalReverb.ERROR_BAD_VALUE} ${environmentalReverb?.enabled} ${environmentalReverb?.roomLevel}")


            exoPlayer.setAuxEffectInfo(AuxEffectInfo(environmentalReverb!!.id, 1f))
        } catch (e: Exception) {
            Logger.d("setEnvironmentalReverb $e")
        }
    }

    override fun disableEnvironmentReverb() {
        Logger.d("disableEnvironmentReverb")
        environmentalReverb?.release()
        environmentalReverb = null
    }

    override fun setHapticGenerator(isEnabled: Boolean) {
        if (isEnabled){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && HapticGenerator.isAvailable()) {
                hapticGenerator = HapticGenerator.create(audioSessionId)
                hapticGenerator?.enabled = true
            }

        }else{
            hapticGenerator?.release()
            hapticGenerator = null
        }



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

        exoPlayer.playbackParameters = PlaybackParameters(currentTempoValue, newPitch)
    }

    override fun tempoPlusOne() {
        val currentPitch = exoPlayer.playbackParameters.pitch
        val currentTempoValue = exoPlayer.playbackParameters.speed

        val currentSemitones = 12 * log2(currentTempoValue.toDouble())

        val newSemitones = currentSemitones + 1

        val newTempo = 2.0.pow(newSemitones / 12.0).toFloat()

        exoPlayer.playbackParameters = PlaybackParameters(newTempo, currentPitch)
    }

    override fun tempoMinusOne() {
        val currentPitch = exoPlayer.playbackParameters.pitch
        val currentTempoValue = exoPlayer.playbackParameters.speed

        val currentSemitones = 12 * log2(currentTempoValue.toDouble())

        val newSemitones = currentSemitones - 1

        val newTempo = 2.0.pow(newSemitones / 12.0).toFloat()

        exoPlayer.playbackParameters = PlaybackParameters(newTempo, currentPitch)
    }
}