package com.example.transpose.media.audio_effect

interface AudioEffectHandler {
    fun setPitch(value: Int)
    fun setTempo(value: Int)
    fun setBassBoost(value: Int)
    fun setLoudnessEnhancer(value: Int)
    fun setEqualizerWithPreset(value: Int)
    fun setEqualizerWithCustomValue(changedBand: Int, newGainLevel: Int)
    fun setVirtualizer(value: Int)
    fun setPresetReverb(presetIndex: Int, sendLevel: Int)
    fun setEnvironmentalReverb()
    fun pitchPlusOne()
    fun pitchMinusOne()
    fun tempoPlusOne()
    fun tempoMinusOne()
}