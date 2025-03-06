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
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.theme.JmTheme

enum class BottomBarItem(
    val id: String,
    val resId: Int,
    val icon: ImageVector,
) {
    Home(id = Navigator.Screens.Home.name, resId = R.string.menu_home, icon = Icons.Default.Home),
    Settings(id = Navigator.Screens.Settings.name, resId = R.string.menu_settings, icon = Icons.Default.Settings),
    Info(id = Navigator.Screens.About.name, resId = R.string.menu_about, icon = Icons.Default.Info);

    companion object {
        fun fromId(id: String): BottomBarItem {
            return when (id) {
                Navigator.Screens.Home.name -> Home
                Navigator.Screens.Settings.name -> Settings
                Navigator.Screens.About.name -> Info
                else -> Home
            }
        }
    }
}


@Composable
fun MjuBottomBar(
    modifier: Modifier = Modifier,
    selected: String = Navigator.Screens.Home.name,
    onItemSelected: (BottomBarItem) -> Unit = {}
) {
    NavigationBar (modifier) {
        BottomBarItem.entries.forEach {
            NavigationBarItem(
                modifier = Modifier,
                selected = it == BottomBarItem.fromId(selected),
                onClick = {
                    onItemSelected(it)
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