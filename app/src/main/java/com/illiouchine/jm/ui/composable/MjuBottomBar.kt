package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.composable.ScreensMenuItem.Home
import com.illiouchine.jm.ui.composable.ScreensMenuItem.About
import com.illiouchine.jm.ui.composable.ScreensMenuItem.Settings
import com.illiouchine.jm.ui.theme.JmTheme

private enum class ScreensMenuItem(
    val screen: Screens,
    val resId: Int,
    val icon: ImageVector,
) {
    Home(screen = Screens.Home, resId = R.string.menu_home, icon = Icons.Default.Home),
    Settings(screen = Screens.Settings, resId = R.string.menu_settings, icon = Icons.Default.Settings),
    About(screen = Screens.About, resId = R.string.menu_about, icon = Icons.Default.Info);
}

private fun Screens.toScreensMenuItem() : ScreensMenuItem {
    return when (this) {
        Screens.Home -> Home
        Screens.Settings -> Settings
        Screens.About -> About
        else -> Home
    }
}

private fun ScreensMenuItem.toScreen(): Screens {
    return this.screen
}

@Composable
fun MjuBottomBar(
    modifier: Modifier = Modifier,
    selected: Screens = Screens.Home,
    onItemSelected: (Screens) -> Unit = {},
) {
    NavigationBar(modifier) {
        ScreensMenuItem.entries.forEach {
            NavigationBarItem(
                modifier = Modifier,
                selected = it == selected.toScreensMenuItem(),
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


@Composable
fun MjuNavigationRail(
    modifier: Modifier = Modifier,
    selected: Screens = Screens.Home,
    onItemSelected: (Screens) -> Unit = {},
) {
    NavigationRail(modifier) {
        Spacer(Modifier.weight(1f))
        ScreensMenuItem.entries.forEach {
            NavigationRailItem(
                modifier = Modifier,
                selected = it == selected.toScreensMenuItem(),
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
        Spacer(Modifier.weight(1f))
    }
}


@Preview
@Composable
private fun PreviewBottomBar() {
    JmTheme {
        Scaffold(
            bottomBar = {
                MjuBottomBar()
            },
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewBottomBarNight() {
    JmTheme {
        Scaffold(
            bottomBar = {
                MjuBottomBar()
            },
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
)
@Composable
private fun PreviewMenuBarVertical() {
    JmTheme {
        Scaffold() {
            Box(modifier = Modifier.padding(it)) {
                MjuNavigationRail()
            }
        }
    }
}
