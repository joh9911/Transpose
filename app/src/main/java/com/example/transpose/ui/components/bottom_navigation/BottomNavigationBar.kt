package com.example.transpose.ui.components.bottom_navigation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.example.transpose.MainViewModel
import com.example.transpose.navigation.viewmodel.NavigationViewModel
import com.example.transpose.ui.components.appbar.SearchWidgetState
import com.example.transpose.utils.constants.AppColors

@Composable
fun BottomNavigationBar(
    navigationViewModel: NavigationViewModel,
    searchWidgetState: SearchWidgetState,
    mainViewModel: MainViewModel
) {
    val normalizedOffset by mainViewModel.normalizedOffset.collectAsState()

    val icons = listOf(BottomNavItem.Home, BottomNavItem.Convert, BottomNavItem.Library)
    val mainNavCurrentRoute by navigationViewModel.mainNavCurrentRoute.collectAsState()


    // BottomSheet에 의한 오프셋 계산
    val bottomSheetOffset = lerp(
        start = 0.dp, stop = 56.dp, fraction = normalizedOffset.coerceIn(0f, 1f)
    )

    // SearchBar 상태에 따른 오프셋 계산
    val searchBarOffset = if (searchWidgetState == SearchWidgetState.OPENED) {
        56.dp
    } else {
        0.dp
    }

    val totalOffset = bottomSheetOffset + searchBarOffset


    BottomNavigation(
        modifier = Modifier
            .navigationBarsPadding()
            .offset(y = totalOffset),
        backgroundColor = AppColors.BlueBackground

    ) {
        icons.forEach { icon ->
            BottomNavigationItem(selected = mainNavCurrentRoute == icon.route, onClick = {
                if (mainNavCurrentRoute == icon.route) {
                    navigationViewModel.resetNavigationFor(icon.route)
                } else {
                    navigationViewModel.changeMainCurrentRoute(icon.route)
                }
            }, icon = {
                Icon(
                    painter = painterResource(
                        id = if (mainNavCurrentRoute == icon.route) icon.filledIcon else icon.outlinedIcon
                    ),
                    tint = Color.White, contentDescription = icon.label,
                )
            }, label = {
                Text(
                    text = icon.label,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = if (mainNavCurrentRoute == icon.route) FontWeight.Bold else null
                )
            })
        }

    }
}

