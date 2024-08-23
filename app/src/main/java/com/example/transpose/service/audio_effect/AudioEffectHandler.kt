package com.example.transpose.service.audio_effect

interface AudioEffectHandler {
    fun setPitch(value: Int)
    fun setTempo(value: Int)
    fun setBassBoost(value: Int)
    fun setLoudnessEnhancer(value: Int)
    fun setEqualizer(value: Int?)
    fun setVirtualizer(value: Int)
    fun setPresetReverb(value: Int, sendLevel: Int)
    fun setEnvironmentalReverb()
}