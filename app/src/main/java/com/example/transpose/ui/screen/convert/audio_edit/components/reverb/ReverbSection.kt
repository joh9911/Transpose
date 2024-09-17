package com.example.transpose.ui.screen.convert.audio_edit.components.reverb

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.ui.screen.convert.audio_edit.components.ExpandableSectionTitle
import com.example.transpose.ui.screen.convert.audio_edit.components.SliderSection
import com.example.transpose.utils.constants.AppColors

@Composable
fun ReverbSection(
    modifier: Modifier = Modifier,
    title: String,
    mediaViewModel: MediaViewModel,
) {
    val reverbValue by mediaViewModel.reverbValue.collectAsState()
    val isEnabled by mediaViewModel.isReverbEnabled.collectAsState()
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clickable() { isExpanded = !isExpanded }
            .fillMaxWidth()
    ) {
        ExpandableSectionTitle(
            isExpanded = isExpanded,
            title = title,
            isEnabled = isEnabled,
            onSwitchChange = {
                mediaViewModel.updateIsReverbEnabled()
                mediaViewModel.disablePreset()
            },
            onInitButton = { mediaViewModel.initEqualizerValue() }
        )

        AnimatedVisibility(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            visible = isExpanded,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.preset_reverb_waring_text),
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                    fontSize = 12.sp,
                    color = AppColors.BlueBackground
                )
                SliderSection(
                    title = "Reverb",
                    displayValueText = "+$reverbValue",
                    onValueChange = { mediaViewModel.updateReverbValue(it) },
                    onValueChangeFinished = { mediaViewModel.setPresetReverb() },
                    onReset = { mediaViewModel.initReverbValue() },
                    currentValue = reverbValue,
                    valueRange = 0f..1000f
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            visible = isExpanded,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ReverbPresetView(mediaViewModel = mediaViewModel)
            }
        }
    }
}