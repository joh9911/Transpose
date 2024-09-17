package com.example.transpose.ui.screen.convert.audio_edit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConvertAudioEditViewModel: ViewModel(){

    private val _bassBoostValue = MutableStateFlow(0)
    val bassBoostValue: StateFlow<Int> get() = _bassBoostValue

    private val _loudnessEnhancerValue = MutableStateFlow(0)
    val loudnessEnhancerValue: StateFlow<Int> get() = _loudnessEnhancerValue

    private val _virtualizerValue = MutableStateFlow(0)
    val virtualizerValue: StateFlow<Int> get() = _virtualizerValue

    private val _presetReverbIndexValue = MutableStateFlow(0)
    val presetReverbIndexValue: StateFlow<Int> get() = _presetReverbIndexValue

    private val _presetReverbSendLevel = MutableStateFlow(0)
    val presetReverbSendLevel: StateFlow<Int> get() = _presetReverbSendLevel

    private val _equalizerIndexValue = MutableStateFlow(3)
    val equalizerIndexValue: StateFlow<Int> get() = _equalizerIndexValue

    private val _isEqualizerEnabled = MutableStateFlow(false)
    val isEqualizerEnabled: StateFlow<Boolean> get() = _isEqualizerEnabled

    private val _isPresetReverbEnabled = MutableStateFlow(false)
    val isPresetReverbEnabled: StateFlow<Boolean> get() = _isPresetReverbEnabled



    fun updateBassBoostValue(value: Int) {
        _bassBoostValue.value = value
    }

    fun updateLoudnessEnhancerValue(value: Int) {
        _loudnessEnhancerValue.value = value
    }

    fun updateVirtualizerValue(value: Int) {
        _virtualizerValue.value = value
    }

    fun updatePresetReverbIndexValue(value: Int) {
        _presetReverbIndexValue.value = value
    }

    fun updatePresetReverbSendLevel(value: Int) {
        _presetReverbSendLevel.value = value
    }

    fun updateEqualizerIndexValue(value: Int) {
        _equalizerIndexValue.value = value
    }

    fun updateIsEqualizerEnabled(value: Boolean) {
        _isEqualizerEnabled.value = value
    }

    fun updateIsPresetReverbEnabled(value: Boolean) {
        _isPresetReverbEnabled.value = value
    }

}