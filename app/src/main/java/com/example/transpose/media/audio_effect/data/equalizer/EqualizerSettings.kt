package com.example.transpose.media.audio_effect.data.equalizer

data class EqualizerSettings(
    val bandLevels: List<Float> = List(5) { 0f },
    val presetName: String = "Custom"
) {


    companion object {
        val BAND_FREQUENCIES = listOf("60Hz", "230Hz", "910Hz", "3kHz", "14kHz")

        fun createFromPreset(presetName: String, levels: List<Float>): EqualizerSettings {
            return EqualizerSettings(levels.map { it * 1000 }, presetName)
        }
    }

    fun getBandLevel(index: Int): Float {
        return bandLevels[index]
    }

    fun withUpdatedBandLevel(index: Int, newLevel: Float): EqualizerSettings {
        return copy(
            bandLevels = bandLevels.toMutableList().also { it[index] = newLevel },
            presetName = if (presetName != "Custom") "Custom" else presetName
        )
    }

    fun toPresetValues(): List<Float> {
        return bandLevels.map { it / 1000 }
    }

    override fun toString(): String {
        return buildString {
            append("Equalizer Settings (Preset: $presetName)\n")
            BAND_FREQUENCIES.forEachIndexed { index, freq ->
                append("$freq: ${bandLevels[index] / 100} dB\n")
            }
        }
    }
}