package com.example.transpose.ui.screen.convert.audio_edit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.transpose.utils.constants.AppColors
import java.util.Locale

@Composable
fun SliderSection(
    title: String,
    displayValueText: String,
    onValueChange: (Int) -> Unit,
    onValueChangeFinished: () -> Unit,
    onReset: () -> Unit,
    currentValue: Int,
    valueRange: ClosedFloatingPointRange<Float>
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 30.dp, bottom = 15.dp)
    ) {
        val (titleText, valueText, resetButton, slider) = createRefs()

        Text(
            text = title,
            fontSize = 14.sp,
            color = AppColors.BlueBackground,
            modifier = Modifier.constrainAs(titleText) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(slider.top)
            }
        )

        Text(
            text = displayValueText,
            fontSize = 14.sp,
            color = AppColors.BlueBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(valueText) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(slider.top)
                }
                .background(Color.Transparent)
        )

        IconButton(
            onClick = onReset,
            modifier = Modifier.constrainAs(resetButton) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(slider.top)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset $title",
                tint = AppColors.BlueBackground
            )
        }

        Slider(
            value = currentValue.toFloat(),
            valueRange = valueRange,
            onValueChange = { onValueChange(it.toInt()) },
            onValueChangeFinished = { onValueChangeFinished() },
            colors = SliderDefaults.colors(
                thumbColor = AppColors.StatusBarBackground,
                activeTrackColor = AppColors.StatusBarBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(slider) {
                    top.linkTo(titleText.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )
    }
}

