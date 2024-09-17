package com.example.transpose.ui.components.bottomsheet.item

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel

@Composable
fun VideoInfoHeader(mediaViewModel: MediaViewModel, mainViewModel: MainViewModel){
    Column {
        VideoInfoSection(mediaViewModel = mediaViewModel)
        ChannelSection(mediaViewModel = mediaViewModel, mainViewModel = mainViewModel)
        PitchControlItem(mediaViewModel = mediaViewModel)
        TempoControlItem(mediaViewModel = mediaViewModel)
    }
}