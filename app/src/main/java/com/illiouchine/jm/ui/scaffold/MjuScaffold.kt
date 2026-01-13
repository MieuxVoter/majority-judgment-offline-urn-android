package com.illiouchine.jm.ui.scaffold

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.MjuNavigationRail
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

/**
 * Purpose
 * -------
 * Show either the NavigationBar or the NavigationRail, depending on the orientation.
 *
 * I don't get how and why this is not part of the core of Compose yet.
 * Perhaps we're doing this wrong.
 */
@Composable
fun MjuScaffold(
    modifier: Modifier = Modifier,
    showMenu: Boolean = false,
    menuModifier: Modifier = Modifier,
    menuItemSelected: Screens = Screens.Home,
    onMenuItemSelected: (Screens) -> Unit = {},
    topBar: @Composable () -> Unit = {},
    // BottomBar property was removed ; use showMenu and menu properties instead
    //bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var bottomBar: @Composable () -> Unit = {}
    if (showMenu && ! isLandscape) {
        bottomBar = {
            MjuBottomBar(
                modifier = menuModifier,
                selected = menuItemSelected,
                onItemSelected = onMenuItemSelected,
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        // One way to update the padding is to update the content window insets,
        // but we're using a magic number here (80) instead of measuring the navigation rail.
        contentWindowInsets = if (showMenu && isLandscape) {
            contentWindowInsets.add(WindowInsets(left=80.dp + Theme.spacing.small))
        } else {
            contentWindowInsets
        },
    ) { contentPadding ->
        if (showMenu && isLandscape) {
            MjuNavigationRail(
                modifier = menuModifier,
                selected = menuItemSelected,
                onItemSelected = onMenuItemSelected,
            )
        }
        content(contentPadding)
    }
}


@Preview(
    showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
)
@Composable
private fun PreviewScaffoldLandscapeNoMenu() {
    JmTheme {
        MjuScaffold {
            Box(modifier = Modifier.padding(it)) {

            }
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
)
@Composable
private fun PreviewScaffoldLandscapeWithMenu() {
    JmTheme {
        MjuScaffold(showMenu = true) {
            Column(modifier = Modifier.padding(it)) {
                Spacer(Modifier.weight(1f))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "I should NOT be behind the vertical menu on the left.",
                    fontStyle = FontStyle.Italic,
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
