package com.example.transpose.ui.components.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.transpose.R
import com.example.transpose.Route

sealed class BottomNavItem(val route: String, val label: String, val filledIcon: Int, val outlinedIcon: Int) {
    data object Home : BottomNavItem(Route.Home.route, "Home", R.drawable.baseline_home_24, R.drawable.outline_home_24)
    data object Convert : BottomNavItem(Route.Convert.route, "Convert", R.drawable.baseline_album_24, R.drawable.outline_album_24)
    data object Library: BottomNavItem(Route.Library.route,"Library", R.drawable.baseline_library_music_24, R.drawable.outline_library_music_24)
}