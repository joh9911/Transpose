package com.example.transpose.ui.screen.convert.audio_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.bassboost.BassBoostSection
import com.example.transpose.ui.screen.convert.audio_edit.components.equalizer.EqualizerSection
import com.example.transpose.ui.screen.convert.audio_edit.components.loudness_enhancer.LoudnessEnhancerSection
import com.example.transpose.ui.screen.convert.audio_edit.components.pitch.PitchSection
import com.example.transpose.ui.screen.convert.audio_edit.components.reverb.ReverbSection
import com.example.transpose.ui.screen.convert.audio_edit.components.tempo.TempoSection
import com.example.transpose.ui.screen.convert.audio_edit.components.virtualizer.VirtualizerSection

@Composable
fun ConvertAudioEditScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    convertAudioEditViewModel: ConvertAudioEditViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PitchSection(
            mediaViewModel = mediaViewModel
        )
        TempoSection(
            mediaViewModel = mediaViewModel)
        EqualizerSection(title = "Equalizer", mediaViewModel = mediaViewModel)
        ReverbSection(title = "Preset", mediaViewModel = mediaViewModel)
        BassBoostSection(
            mediaViewModel = mediaViewModel)
        LoudnessEnhancerSection(
            mediaViewModel = mediaViewModel)
        VirtualizerSection(
            mediaViewModel = mediaViewModel)
    }


}

