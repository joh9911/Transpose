package com.example.transpose.media.audio_effect

interface AudioEffectHandler {
    fun setPitch(value: Int)
    fun setTempo(value: Int)
    fun setBassBoost(value: Int)
    fun disableBassBoost()
    fun setLoudnessEnhancer(value: Int)
    fun disableLoudnessEnhancer()
    fun setEqualizerWithPreset(value: Int)
    fun setEqualizerWithCustomValue(changedBand: Int, newGainLevel: Int)
    fun disableEqualizer()
    fun setVirtualizer(value: Int)
    fun disableVirtualizer()
    fun setPresetReverb(presetIndex: Int, sendLevel: Int)
    fun disableReverb()
    fun setEnvironmentalReverb(
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
    )
    fun disableEnvironmentReverb()
    fun setHapticGenerator(isEnabled: Boolean)
    fun pitchPlusOne()
    fun pitchMinusOne()
    fun tempoPlusOne()
    fun tempoMinusOne()
}