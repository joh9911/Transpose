package com.example.transpose.ui.screen.convert.audio_edit.components.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.MediaViewModel
import com.example.transpose.utils.constants.AppColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerView(mediaViewModel: MediaViewModel) {

    val equalizerCurrentPreset by mediaViewModel.equalizerCurrentPreset.collectAsState()
    val equalizerSettings by mediaViewModel.equalizerSettings.collectAsState()

    val xAxisLabels = listOf("60Hz", "230Hz", "910Hz", "3kHz", "14kHz")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer {
                rotationZ = 270f
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (index in xAxisLabels.indices) {
            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .width(220.dp)
            ) {
                // Each frequency label and its corresponding slider are placed in a Box
                BoxWithConstraints {
                    val sliderWidth = maxWidth

                    // Display the frequency label with rotation
                    Text(
                        text = xAxisLabels[index],
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterStart)
                            .rotate(90f),
                        color = AppColors.BlueBackground,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Start
                    )

                    // Slider component for adjusting the gain value of each frequency band
                    Slider(
                        modifier = Modifier
                            .offset(x = 20.dp),
                        // Bind the slider value to the corresponding gain value from the ViewModel
                        value = equalizerSettings.bandLevels[index],
                        onValueChange = { newValue ->
                            mediaViewModel.updateEqualizerBandLevel(index, newValue)
                        },
                        onValueChangeFinished = {
                            mediaViewModel.setEqualizerWithCustomValue(index)
                        },
                        valueRange = -1500f..1500f,
                        colors = SliderDefaults.colors(
                            thumbColor = AppColors.StatusBarBackground,
                            activeTrackColor = AppColors.BlueBackgroundAlpha30,
                            inactiveTrackColor = AppColors.BlueBackgroundAlpha30
                        ),
                        thumb = {
                            // Customized appearance of the slider's thumb
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(
                                        1.dp,
                                        Color.Black,
                                        CircleShape
                                    )
                                    .clip(CircleShape)
                                    .background(AppColors.StatusBarBackground, CircleShape)
                            )
                        }
                    )
                    Text(
                        text = if (equalizerSettings.bandLevels[index] >= 0)
                            String.format(Locale.ROOT, "+%.1f", equalizerSettings.bandLevels[index])
                                    else
                            String.format(Locale.ROOT, "%.1f", equalizerSettings.bandLevels[index]),
                        modifier = Modifier
                            .offset(x = 20.dp + sliderWidth)
                            .wrapContentWidth()
                            .align(Alignment.CenterStart)
                            .rotate(90f),
                        color = AppColors.BlueBackground,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}