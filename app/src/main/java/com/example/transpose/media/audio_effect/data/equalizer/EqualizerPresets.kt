package com.example.transpose.media.audio_effect.data.equalizer

object EqualizerPresets {
    // Equalizer presets
    val effectTypes = listOf(
        "Normal", "Classical", "Dance", "Default",
        "Folk", "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock"
    )

    // Constants for presets
    const val PRESET_NORMAL = 0
    const val PRESET_CLASSICAL = 1
    const val PRESET_DANCE = 2
    const val PRESET_DEFAULT = 3
    const val PRESET_FOLK = 4
    const val PRESET_HEAVY_METAL = 5
    const val PRESET_HIP_HOP = 6
    const val PRESET_JAZZ = 7
    const val PRESET_POP = 8
    const val PRESET_ROCK = 9

    // Gain values for each preset (multiplied by 100 to convert to float)
    val NORMAL = listOf(300, 0, 0, 0, 300)
    val CLASSICAL = listOf(500, 300, -200, 400, 400)
    val DANCE = listOf(600, 0, 200, 400, 100)
    val DEFAULT = listOf(0, 0, 0, 0, 0)
    val FOLK = listOf(300, 0, 0, 200, -100)
    val HEAVY_METAL = listOf(400, 100, 900, 300, 0)
    val HIP_HOP = listOf(500, 300, 0, 100, 300)
    val JAZZ = listOf(400, 200, -200, 200, 500)
    val POP = listOf(-100, 200, 500, 100, -200)
    val ROCK = listOf(500, 300, -100, 300, 500)

    // Function to get gain values by preset index
    fun getPresetGainValues(presetIndex: Int): List<Int> {
        return when (presetIndex) {
            PRESET_NORMAL -> NORMAL
            PRESET_CLASSICAL -> CLASSICAL
            PRESET_DANCE -> DANCE
            PRESET_DEFAULT -> DEFAULT
            PRESET_FOLK -> FOLK
            PRESET_HEAVY_METAL -> HEAVY_METAL
            PRESET_HIP_HOP -> HIP_HOP
            PRESET_JAZZ -> JAZZ
            PRESET_POP -> POP
            PRESET_ROCK -> ROCK
            else -> DEFAULT
        }
    }
}