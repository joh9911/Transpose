package com.example.transpose.ui.screen.convert.audio_edit.components.haptic_generator

import android.media.audiofx.HapticGenerator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.MediaViewModel

@Composable
fun HapticGeneratorSection(
    mediaViewModel: MediaViewModel
) {

    val isEnabled by mediaViewModel.isHapticGeneratorEnabled.collectAsState()

    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(

            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "HapticGenerator", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Row{
            Switch(
                checked = isEnabled,
                onCheckedChange = { mediaViewModel.updateIsHapticGenerator() }
            )

        }



    }
}