package com.example.transpose.ui.screen.convert.audio_edit.components.tempo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.SliderSection
import java.util.Locale

@Composable
fun TempoSection(mediaViewModel: MediaViewModel) {
    val tempoValue by mediaViewModel.tempoValue.collectAsState()
    val actualValue = (tempoValue * 0.1) - 10.0

    val displayText = if (actualValue >= 0) {
        String.format(Locale.ROOT, "+%.1f", actualValue)
    } else {
        String.format(Locale.ROOT, "%.1f", actualValue)
    }

    SliderSection(
        title = "Tempo",
        displayValueText = displayText,
        onValueChange = { mediaViewModel.updateTempoValue(it) },
        onValueChangeFinished = {mediaViewModel.setTempo()},
        onReset = { mediaViewModel.initTempoValue() },
        currentValue = tempoValue,
        valueRange = 0f..200f
    )
}