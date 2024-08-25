package com.example.transpose.ui.screen.convert.audio_edit.components.loudness_enhancer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.SliderSection

@Composable
fun LoudnessEnhancerSection(
    mediaViewModel: MediaViewModel
) {
    val loudnessEnhancerValue by mediaViewModel.loudnessEnhancerValue.collectAsState()

    SliderSection(
        title = "Loudness Enhancer",
        displayValueText = "+$loudnessEnhancerValue",
        onValueChange = {mediaViewModel.updateLoudnessEnhancerValue(it)},
        onValueChangeFinished = {mediaViewModel.setLoudnessEnhancer()},
        onReset = { mediaViewModel.initLoudnessEnhancerValue() },
        currentValue = loudnessEnhancerValue,
        valueRange = 0f..6000f
    )

}