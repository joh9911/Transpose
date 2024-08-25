package com.example.transpose.ui.screen.convert.audio_edit.components.tempo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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