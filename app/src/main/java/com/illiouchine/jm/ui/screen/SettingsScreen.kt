package com.illiouchine.jm.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.isShowingTextSubstitution
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    settingsState: SettingsViewModel.SettingsViewState = SettingsViewModel.SettingsViewState(),
    feedback: String? = "",
    onShowOnBoardingRequested: () -> Unit = {},
    onPlaySoundChange: (Boolean) -> Unit = {},
    onPinScreenChange: (Boolean) -> Unit = {},
    onDefaultGradingSelected: (Grading) -> Unit = {},
    onDismissFeedback: () -> Unit = {},
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
                onItemSelected = { destination -> navController.navigate(destination) },
            )
        },
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .verticalScroll(state = scrollState)
                .padding(innerPadding),
        ) {
            ScreenTitle(
                modifier = Modifier,
                text = stringResource(R.string.settings_screen_title),
            )

            ShowOnboardingRow(
                modifier = Modifier.padding(Theme.spacing.medium),
                title = R.string.setting_show_onboarding,
                onRequestToShowOnboarding = {
                    onShowOnBoardingRequested()
                },
            )

            SwitchSettingRow(
                modifier = Modifier.padding(Theme.spacing.medium),
                title = R.string.setting_play_sound,
                label = R.string.setting_play_sound_label,
                checked = settingsState.playSound,
                onCheckedChange = {
                    onPlaySoundChange(it)
                },
            )

            SwitchSettingRow(
                modifier = Modifier.padding(Theme.spacing.medium),
                title = R.string.setting_pin_screen,
                label = R.string.setting_pin_screen_label,
                checked = settingsState.pinScreen,
                onCheckedChange = {
                    onPinScreenChange(it)
                },
            )

            GradingSelectionRow(
                modifier = Modifier,
                grading = settingsState.defaultGrading,
                onGradingSelected = { onDefaultGradingSelected(it) },
            )
        }
    }
}

@Composable
fun ShowOnboardingRow(
    title: Int,
    modifier: Modifier = Modifier,
    onRequestToShowOnboarding: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val titleText = stringResource(title)
        Text(
            modifier = Modifier.semantics {
                invisibleToUser()
            },
            text = titleText,
        )
        OutlinedButton(
            modifier = Modifier
                .semantics {
                    onClick(
                        label = titleText,
                        action = null,
                    )
                },
            onClick = {
                onRequestToShowOnboarding()
            },
        ) {
            Text(stringResource(R.string.setting_show_onboarding_button))
        }
    }
}

@Composable
fun SwitchSettingRow(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes label: Int = 0,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    val activateText = stringResource(R.string.tts_activate)
    val deactivateText = stringResource(R.string.tts_deactivate)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (onCheckedChange != null) {
                    onCheckedChange(!checked)
                }
            }
            .semantics(mergeDescendants = true) {
                contentDescription = "setting"
                onClick(
                    label = if (checked) {
                        deactivateText
                    } else {
                        activateText
                    },
                    action = null,
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .padding(end = Theme.spacing.extraSmall)
                .weight(1.0f),
        ) {
            Text(stringResource(title))
            if (label != 0) {
                Text(
                    text = stringResource(label),
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                )
            }
        }
        Switch(
            modifier = Modifier.semantics {
                invisibleToUser()
            },
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    JmTheme {
        SettingsScreen()
    }
}
