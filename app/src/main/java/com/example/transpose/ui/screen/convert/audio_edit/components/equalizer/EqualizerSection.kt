package com.example.transpose.ui.screen.convert.audio_edit.components.equalizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
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


@Composable
fun EqualizerSection(
    modifier: Modifier = Modifier,
    title: String,
    mediaViewModel: MediaViewModel,
) {
    val isEnabled by mediaViewModel.isEqualizerEnabled.collectAsState()
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
            onSwitchChange = { mediaViewModel.updateIsEqualizerEnabled() },
            onInitButton = { mediaViewModel.initEqualizerValue()}
            )

        AnimatedVisibility(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            visible = isExpanded,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                EqualizerView(mediaViewModel = mediaViewModel)
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            visible = isExpanded,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                EqualizerPresetView(mediaViewModel = mediaViewModel)
            }
        }
    }
}

