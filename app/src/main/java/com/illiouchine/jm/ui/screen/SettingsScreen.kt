package com.illiouchine.jm.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.composable.GradingSelectionRow
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    settingsState: SettingsViewModel.SettingsViewState = SettingsViewModel.SettingsViewState(),
    feedback: String? = "",
    onShowOnboardingChange: (Boolean) -> Unit = {},
    onPlaySoundChange: (Boolean) -> Unit = {},
    onPinScreenChange: (Boolean) -> Unit = {},
    onDefaultGradingSelected: (Grading) -> Unit = {},
    onDismissFeedback: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_settings"),
        snackbarHost = {
            MjuSnackbar(
                modifier = Modifier,
                text = feedback,
                onDismiss = {
                    onDismissFeedback()
                },
            )
        },
        bottomBar = {
            MjuBottomBar(
                modifier = Modifier,
                selected = Screens.Settings,
                onItemSelected = { destination -> navController.navigate(destination) }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(state = ScrollState(initial = 0))
                .padding(innerPadding),
        ) {
            ScreenTitle(
                modifier = Modifier,
                text = stringResource(R.string.settings_screen_title),
            )

            SwitchSettingRow(
                title = R.string.setting_show_onboarding,
                checked = settingsState.showOnboarding,
                onCheckedChange = {
                    onShowOnboardingChange(it)
                },
            )

            SwitchSettingRow(
                title = R.string.setting_play_sound,
                checked = settingsState.playSound,
                onCheckedChange = {
                    onPlaySoundChange(it)
                },
            )

            SwitchSettingRow(
                title = R.string.setting_pin_screen,
                checked = settingsState.pinScreen,
                onCheckedChange = {
                    onPinScreenChange(it)
                },
            )

            GradingSelectionRow(
                modifier = Modifier,
                grading = settingsState.defaultGrading,
                onGradingSelected = { onDefaultGradingSelected(it) }
            )
        }
    }
}

@Composable
fun SwitchSettingRow(
    @StringRes title: Int,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(title))
        Switch(
            modifier = Modifier,
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}


@Preview(showSystemUi = true)
@Composable
private fun PreviewSettingsScreen() {
    JmTheme {
        SettingsScreen()
    }
}