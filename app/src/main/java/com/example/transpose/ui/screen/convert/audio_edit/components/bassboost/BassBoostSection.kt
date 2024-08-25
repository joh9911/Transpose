package com.example.transpose.ui.screen.convert.audio_edit.components.bassboost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.SliderSection

@Composable
fun BassBoostSection(mediaViewModel: MediaViewModel) {
    val bassBoostValue by mediaViewModel.bassBoostValue.collectAsState()
    SliderSection(
        title = "Bass",
        displayValueText = "+$bassBoostValue",
        onValueChange = { mediaViewModel.updateBassBoostValue(it) },
        onValueChangeFinished = { mediaViewModel.setBassBoost() },
        onReset = { mediaViewModel.initBassBoostValue() },
        currentValue = bassBoostValue,
        valueRange = 0f..1000f
    )
}