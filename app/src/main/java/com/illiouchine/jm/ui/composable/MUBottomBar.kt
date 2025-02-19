package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.JmTheme

enum class BottomBarItem(
    val resId: Int,
    val icon: ImageVector
) {
    Home(resId = R.string.bottombar_home, icon = Icons.Default.Home),
    Settings(resId = R.string.bottombar_settings, icon = Icons.Default.Settings)
}


@Composable
fun MUBottomBar(
    modifier: Modifier = Modifier,
    selected: BottomBarItem = BottomBarItem.Home
) {
    NavigationBar {
        BottomBarItem.entries.forEach {
            NavigationBarItem(
                modifier = Modifier,
                selected = it == selected,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = stringResource(it.resId),
                        modifier = Modifier,
                        tint = LocalContentColor.current
                    )
                },
                enabled = true,
                label = { Text(stringResource(it.resId)) },
                alwaysShowLabel = true
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBottomBar() {
    JmTheme {
        Scaffold(
            bottomBar = {
                MUBottomBar()
            }
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}