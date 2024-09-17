package com.example.transpose.ui.screen.convert.audio_edit.components.environmental_reverb

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.transpose.MediaViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.ExpandableSectionTitle
import com.example.transpose.ui.screen.convert.audio_edit.components.SliderSection


@Composable
fun EnvironmentalReverbSection(
    mediaViewModel: MediaViewModel,
) {
    val isEnabled by mediaViewModel.isEnvironmentalReverbEnabled.collectAsState()
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val roomLevel by mediaViewModel.roomLevel.collectAsState()
    val roomHFLevel by mediaViewModel.roomHFLevel.collectAsState()
    val decayTime by mediaViewModel.decayTime.collectAsState()
    val decayHFRatio by mediaViewModel.decayHFRatio.collectAsState()
    val reflectionsLevel by mediaViewModel.reflectionsLevel.collectAsState()
    val reflectionsDelay by mediaViewModel.reflectionsDelay.collectAsState()
    val reverbLevel by mediaViewModel.reverbLevel.collectAsState()
    val reverbDelay by mediaViewModel.reverbDelay.collectAsState()
    val diffusion by mediaViewModel.diffusion.collectAsState()
    val density by mediaViewModel.density.collectAsState()

    Column(
        modifier = Modifier
            .clickable() { isExpanded = !isExpanded }
            .fillMaxWidth()
    ) {
        ExpandableSectionTitle(
            isExpanded = isExpanded,
            title = "Environmental Reverb",
            isEnabled = isEnabled,
            onSwitchChange = {
                mediaViewModel.updateIsEnvironmentalReverbEnabled(it)
                mediaViewModel.disableEqualizer()
            },
            onInitButton = { mediaViewModel.initEnvironmentalReverbValues() }
        )

        AnimatedVisibility(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            visible = isExpanded,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SliderSection(
                    title = "Room Level",
                    displayValueText = "$roomLevel mB",
                    onValueChange = { mediaViewModel.updateRoomLevel(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initRoomLevel() },
                    currentValue = roomLevel,
                    valueRange = -9000f..0f
                )

                SliderSection(
                    title = "Room HF Level",
                    displayValueText = "$roomHFLevel mB",
                    onValueChange = { mediaViewModel.updateRoomHFLevel(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initRoomHFLevel() },
                    currentValue = roomHFLevel,
                    valueRange = -9000f..0f
                )

                SliderSection(
                    title = "Decay Time",
                    displayValueText = "$decayTime ms",
                    onValueChange = { mediaViewModel.updateDecayTime(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initDecayTime() },
                    currentValue = decayTime,
                    valueRange = 100f..20000f
                )

                SliderSection(
                    title = "Decay HF Ratio",
                    displayValueText = "$decayHFRatio ‰",
                    onValueChange = { mediaViewModel.updateDecayHFRatio(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initDecayHFRatio() },
                    currentValue = decayHFRatio,
                    valueRange = 100f..1000f
                )

                SliderSection(
                    title = "Reflections Level",
                    displayValueText = "$reflectionsLevel mB",
                    onValueChange = { mediaViewModel.updateReflectionsLevel(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initReflectionsLevel() },
                    currentValue = reflectionsLevel,
                    valueRange = -9000f..1000f
                )

                SliderSection(
                    title = "Reflections Delay",
                    displayValueText = "$reflectionsDelay ms",
                    onValueChange = { mediaViewModel.updateReflectionsDelay(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initReflectionsDelay() },
                    currentValue = reflectionsDelay,
                    valueRange = 0f..300f
                )

                SliderSection(
                    title = "Reverb Level",
                    displayValueText = "$reverbLevel mB",
                    onValueChange = { mediaViewModel.updateReverbLevel(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initReverbLevel() },
                    currentValue = reverbLevel,
                    valueRange = -9000f..2000f
                )

                SliderSection(
                    title = "Reverb Delay",
                    displayValueText = "$reverbDelay ms",
                    onValueChange = { mediaViewModel.updateReverbDelay(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initReverbDelay() },
                    currentValue = reverbDelay,
                    valueRange = 0f..100f
                )

                SliderSection(
                    title = "Diffusion",
                    displayValueText = "$diffusion ‰",
                    onValueChange = { mediaViewModel.updateDiffusion(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initDiffusion() },
                    currentValue = diffusion,
                    valueRange = 0f..1000f
                )

                SliderSection(
                    title = "Density",
                    displayValueText = "$density ‰",
                    onValueChange = { mediaViewModel.updateDensity(it) },
                    onValueChangeFinished = { mediaViewModel.setEnvironmentalReverb() },
                    onReset = { mediaViewModel.initDensity() },
                    currentValue = density,
                    valueRange = 0f..1000f
                )
            }        }

    }
}

