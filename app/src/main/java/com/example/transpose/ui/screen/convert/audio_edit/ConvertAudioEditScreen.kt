package com.example.transpose.ui.screen.convert.audio_edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.R
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.screen.convert.audio_edit.components.bassboost.BassBoostSection
import com.example.transpose.ui.screen.convert.audio_edit.components.equalizer.EqualizerSection
import com.example.transpose.ui.screen.convert.audio_edit.components.loudness_enhancer.LoudnessEnhancerSection
import com.example.transpose.ui.screen.convert.audio_edit.components.pitch.PitchSection
import com.example.transpose.ui.screen.convert.audio_edit.components.reverb.ReverbSection
import com.example.transpose.ui.screen.convert.audio_edit.components.tempo.TempoSection
import com.example.transpose.ui.screen.convert.audio_edit.components.virtualizer.VirtualizerSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertAudioEditScreen(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navigationViewModel: NavigationViewModel,
    convertAudioEditViewModel: ConvertAudioEditViewModel
) {

    val bottomSheetState by mainViewModel.bottomSheetState.collectAsState()

    BackHandler(
        enabled = bottomSheetState == SheetValue.Expanded
    ) {
        mainViewModel.partialExpandBottomSheet()
    }

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
        Spacer(modifier = Modifier.height(10.dp))

        EqualizerSection(title = stringResource(id = R.string.equalizer_text), mediaViewModel = mediaViewModel)
        Spacer(modifier = Modifier.height(10.dp))
        ReverbSection(title = stringResource(id = R.string.preset_reverb_text), mediaViewModel = mediaViewModel)
        BassBoostSection(
            mediaViewModel = mediaViewModel)
        LoudnessEnhancerSection(
            mediaViewModel = mediaViewModel)
        VirtualizerSection(
            mediaViewModel = mediaViewModel)
//        HapticGeneratorSection(mediaViewModel = mediaViewModel)
//        EnvironmentalReverbSection(mediaViewModel = mediaViewModel)
    }


}

