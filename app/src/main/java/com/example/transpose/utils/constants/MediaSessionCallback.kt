package com.example.transpose.utils.constants

object MediaSessionCallback{

    private const val prefix = "transpose_"
    const val UPDATE_METADATA = prefix + "updateMetaData"
    const val SET_PITCH = prefix +"setPitch"
    const val SET_TEMPO = prefix +"setTempo"
    const val GET_EQUALIZER_INFO = prefix + "setEqualizerInfo"
    const val SET_EQUALIZER_PRESET = prefix + "setEqualizerPreset"
    const val SET_EQUALIZER_CUSTOM = prefix + "setEqualizerCustom"
    const val DISABLE_EQUALIZER = prefix + "disableEqualizer"
    const val SET_BASS_BOOST = prefix + "setBassBoost"
    const val SET_LOUDNESS_ENHANCER= prefix + "setLoudnessEnhancer"
    const val SET_VIRTUALIZER = prefix + "setVirtualizer"
    const val SET_REVERB = prefix + "setReverb"
    const val DISABLE_REVERB = prefix + "disableReverb"
    const val SET_ENVIRONMENT_REVERB = prefix + "setEnvironmentReverb"

    const val PITCH_MINUS = prefix + "pitchMinus"
    const val PITCH_PLUS = prefix + "pitchPlus"
    const val TEMPO_MINUS = prefix + "tempoMinus"
    const val TEMPO_PLUS = prefix + "tempoPlus"
    const val SET_HAPTIC_GENERATOR = prefix + "setHapticGenerator"


}