package com.example.transpose.ui.screen.convert.audio_edit.components.virtualizer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.SliderSection

@Composable
fun VirtualizerSection(
    mediaViewModel: MediaViewModel
) {
    val virtualizerValue by mediaViewModel.virtualizerValue.collectAsState()
    SliderSection(
        title = "Virtualizer",
        displayValueText = "+$virtualizerValue",
        onValueChange = {mediaViewModel.updateVirtualizerValue(it)},
        onValueChangeFinished = {mediaViewModel.setVirtualizer()},
        onReset = { mediaViewModel.initVirtualizerValue()},
        currentValue = virtualizerValue,
        valueRange = 0f.. 1000f
    )
}