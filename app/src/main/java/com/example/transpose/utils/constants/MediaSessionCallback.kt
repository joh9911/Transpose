package com.example.transpose.utils.constants

object MediaSessionCallback{

    private const val prefix = "transpose_"
    const val SET_PITCH = "setPitch"
    const val GET_EQUALIZER_INFO = prefix + "setEqualizerInfo"
    const val SET_EQUALIZER = prefix + "setEqualizer"
    const val SET_BASS_BOOST = prefix + "setBassBoost"
    const val SET_LOUDNESS_ENHANCER= prefix + "setLoudnessEnhancer"
    const val SET_VIRTUALIZER = prefix + "setVirtualizer"
    const val SET_REVERB = prefix + "setReverb"
    const val SET_ENVIRONMENT_REVERB = prefix + "setEnvironmentReverb"
    const val MAIN = prefix + "main"
    const val PREV = prefix + "prev"
    const val NEXT = prefix + "next"
    const val PLAY = prefix + "play"
    const val INIT = prefix + "init"
    const val MINUS = prefix + "minus"
    const val PLUS = prefix + "plus"
    const val REPLAY = prefix + "replay"
    const val PAUSE = prefix + "pause"
    const val START_FOREGROUND = prefix + "startforeground"
    const val STOP_FOREGROUND = prefix + "stopforeground"

}