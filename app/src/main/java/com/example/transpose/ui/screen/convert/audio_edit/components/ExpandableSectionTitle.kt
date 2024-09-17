package com.example.transpose.ui.screen.convert.audio_edit.components

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
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transpose.utils.constants.AppColors

@Composable
fun ExpandableSectionTitle(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    title: String,
    isEnabled: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    onInitButton: () -> Unit
) {

    val icon = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown

    Row(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(

            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.BlueBackground
            )
            Icon(
                modifier = Modifier.padding(horizontal = 10.dp),
                imageVector = icon,
                contentDescription = "arrow",
            )
        }
        val thumbColor = if (isEnabled) {
            AppColors.BlueBackground
        } else {
            Color.White
        }

        val trackColor = if (isEnabled) {
            AppColors.BlueBackgroundAlpha30
        } else {
            AppColors.LightGray
        }

        Row {
            Switch(
                checked = isEnabled,
                onCheckedChange = { onSwitchChange(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = thumbColor,
                    checkedTrackColor = trackColor,
                    uncheckedThumbColor = thumbColor,
                    uncheckedTrackColor = trackColor,
                    uncheckedBorderColor = Color.White,

                    )
            )
            IconButton(modifier = Modifier.padding(start = 10.dp), onClick = { onInitButton() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Equalizer",
                    tint = AppColors.BlueBackground
                )
            }
        }


    }
}