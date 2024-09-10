package com.example.transpose.ui.components.dropdown_menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DropDownMenu(
    text: String,
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit
){

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { onDismissRequest()}
    ) {
        DropdownMenuItem(
            text = { Text(text) },
            onClick = {
                onClick()
                onDismissRequest()
            }
        )
    }
}