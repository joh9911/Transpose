package com.example.transpose.ui.components.bottomnavigation

import android.graphics.drawable.Icon
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.transpose.NavigationViewModel
import com.example.transpose.Route
import com.example.transpose.ui.components.appbar.SearchWidgetState
import com.example.transpose.utils.Logger

@Composable
fun BottomNavigationBar(
    navigationViewModel: NavigationViewModel,
    searchWidgetState: SearchWidgetState
) {
    val icons = listOf(BottomNavItem.Home, BottomNavItem.Convert, BottomNavItem.Library)
    val mainNavCurrentRoute by navigationViewModel.mainNavCurrentRoute.collectAsState()

    val density = LocalDensity.current.density
    val bottomBarHeight = 56.dp

    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.toPx() }
    val transitionPx = -56 * density


    BottomNavigation(
        modifier = when(searchWidgetState){
            SearchWidgetState.OPENED -> Modifier.navigationBarsPadding().offset(y = 56.dp)
            SearchWidgetState.CLOSED -> Modifier.navigationBarsPadding()
        }

    ) {
        icons.forEach { icon ->
            BottomNavigationItem(
                selected = mainNavCurrentRoute == icon.route,
                onClick = { navigationViewModel.changeMainCurrentRoute(icon.route) },
                icon = { Icon(
                    painter = painterResource(
                        id = if (mainNavCurrentRoute == icon.route) icon.filledIcon else icon.outlinedIcon
                    ),
                    contentDescription = icon.label
                ) },
                label = {Text(icon.label)}
            )
        }

    }
}

