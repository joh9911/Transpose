package com.example.transpose.ui.components.bottomsheet.item

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.transpose.MainViewModel
import com.example.transpose.MediaViewModel
import com.example.transpose.data.model.newpipe.NewPipeVideoData

@Composable
fun VideoInfoHeader(mediaViewModel: MediaViewModel, mainViewModel: MainViewModel){
    Column {
        VideoInfoSection(mediaViewModel = mediaViewModel)
        ChannelSection(mediaViewModel = mediaViewModel, mainViewModel = mainViewModel)
        PitchControlItem(mediaViewModel = mediaViewModel)
        TempoControlItem(mediaViewModel = mediaViewModel)
    }
}