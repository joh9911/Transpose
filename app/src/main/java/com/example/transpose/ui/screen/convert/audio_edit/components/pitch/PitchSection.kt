package com.example.transpose.ui.screen.convert.audio_edit.components.pitch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.SliderSection
import java.util.Locale

@Composable
fun PitchSection(mediaViewModel: MediaViewModel) {
    val pitchValue by mediaViewModel.pitchValue.collectAsState()

    val actualValue = (pitchValue * 0.1) - 10.0

    val displayText = if (actualValue >= 0) {
        String.format(Locale.ROOT, "+%.1f", actualValue)
    } else {
        String.format(Locale.ROOT, "%.1f", actualValue)
    }
    SliderSection(
        title = "Pitch",
        displayValueText = displayText,
        onValueChange = { mediaViewModel.updatePitchValue(it) },
        onValueChangeFinished = {mediaViewModel.setPitch()},
        onReset = { mediaViewModel.initPitchValue() },
        currentValue = pitchValue,
        valueRange = 0f..200f
    )
}