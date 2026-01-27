package com.illiouchine.jm.ui.composable.scaffold

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
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
 * Other than that, act like the base Scaffold.
 *
 * I don't get how and why this is not part of the core of Compose yet.
 * Perhaps we're doing this wrong?
 */
@Composable
fun MjuScaffold(
    modifier: Modifier = Modifier,
    // Custom properties
    showMenu: Boolean = false,
    menuModifier: Modifier = Modifier,
    menuItemSelected: Screens = Screens.Home,
    onMenuItemSelected: (Screens) -> Unit = {},
    // -----------------
    topBar: @Composable () -> Unit = {},
    // BottomBar property was removed ; use showMenu and menu properties instead
    // IDEA: re-enable the property and yell/throw on use ?
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
    if (showMenu && !isLandscape) {
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
        contentWindowInsets = contentWindowInsets,
    ) { originalPadding ->
        if (showMenu && isLandscape) {
            // NOTE: the nav rail will always appear on the left, whatever the layout direction is.
            // Therefore we need to compensate for this, as the PaddingValues API is start/end.
            val layoutDirection = LocalLayoutDirection.current
            val isLtr = (layoutDirection == LayoutDirection.Ltr)
            // WARN: We're using a magic number here (80) instead of measuring the navigation rail.
            // Since we create the nav rail at the very end I'm not even sure how we might do that.
            val railWidth = 80.dp + Theme.spacing.small
            // Let's update the padding to include our vertical navigation rail
            val actualPadding = PaddingValues(
                start = originalPadding.calculateStartPadding(layoutDirection) + if (isLtr) {
                    railWidth
                } else {
                    0.dp
                },
                top = originalPadding.calculateTopPadding(),
                end = originalPadding.calculateEndPadding(layoutDirection) + if (isLtr) {
                    0.dp
                } else {
                    railWidth
                },
                bottom = originalPadding.calculateBottomPadding(),
            )
            // We must process the content first, so that our navigation rail has a "higher zIndex".
            // (otherwise it does appear but the user cannot interact with it → soft lock)
            // The behavior appears inconsistent, so it might cause trouble down the line.
            // If it ever fails, just assign a high zIndex to the modifier of the rail?
            content(actualPadding)
            MjuNavigationRail(
                modifier = menuModifier,
                selected = menuItemSelected,
                onItemSelected = onMenuItemSelected,
            )
        } else {
            content(originalPadding)
        }
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
                    text = "I should NOT be behind the vertical menu on the left, and yet I should probably go to the right edge of the preview.",
                    fontStyle = FontStyle.Italic,
                )
                Spacer(Modifier.weight(1f))
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
private fun PreviewScaffoldLandscapeWithMenuRtl() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        JmTheme {
            MjuScaffold(showMenu = true) {
                Column(modifier = Modifier.padding(it)) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "I should NOT be behind the vertical menu, I should be filled with joy and hot muffins, and the ever-nagging sense of doom that never fails to accompany my fellow contemporaries.",
                        fontStyle = FontStyle.Italic,
                    )
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
