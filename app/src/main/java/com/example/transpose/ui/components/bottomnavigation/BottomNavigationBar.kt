package com.example.transpose.ui.components.bottomnavigation

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.transpose.NavigationViewModel
import com.example.transpose.Route

@Composable
fun BottomNavigationBar(
    navigationViewModel: NavigationViewModel,
) {
    val icons = listOf(BottomNavItem.Home, BottomNavItem.Convert, BottomNavItem.Library)
    val mainNavCurrentRoute by navigationViewModel.mainNavCurrentRoute.collectAsState()
    BottomNavigation(
       modifier =  Modifier.navigationBarsPadding()
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
                ) }
            )
        }

    }
}

