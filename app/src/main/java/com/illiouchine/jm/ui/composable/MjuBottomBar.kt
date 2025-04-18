package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
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
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.composable.BottomBarItem.Home
import com.illiouchine.jm.ui.composable.BottomBarItem.Info
import com.illiouchine.jm.ui.composable.BottomBarItem.Settings
import com.illiouchine.jm.ui.theme.JmTheme

private enum class BottomBarItem(
    val screen: Screens,
    val resId: Int,
    val icon: ImageVector,
) {
    Home(screen = Screens.Home, resId = R.string.menu_home, icon = Icons.Default.Home),
    Settings(screen = Screens.Settings, resId = R.string.menu_settings, icon = Icons.Default.Settings),
    Info(screen = Screens.About, resId = R.string.menu_about, icon = Icons.Default.Info);
}

private fun Screens.toBottomBarItem() : BottomBarItem {
    return when (this) {
        Screens.Home -> Home
        Screens.Settings -> Settings
        Screens.About -> Info
        else -> Home
    }
}

private fun BottomBarItem.toScreen(): Screens {
    return when (this) {
        Home -> Screens.Home
        Settings -> Screens.Settings
        Info -> Screens.About
    }
}

@Composable
fun MjuBottomBar(
    modifier: Modifier = Modifier,
    selected: Screens = Screens.Home,
    onItemSelected: (Screens) -> Unit = {}
) {
    NavigationBar (modifier) {
        BottomBarItem.entries.forEach {
            NavigationBarItem(
                modifier = Modifier,
                selected = it == selected.toBottomBarItem(),
                onClick = {
                    onItemSelected(it.toScreen())
                },
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = stringResource(it.resId),
                        modifier = Modifier,
                        tint = LocalContentColor.current,
                    )
                },
                enabled = true,
                label = { Text(stringResource(it.resId)) },
                alwaysShowLabel = true,
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
                MjuBottomBar()
            }
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}
