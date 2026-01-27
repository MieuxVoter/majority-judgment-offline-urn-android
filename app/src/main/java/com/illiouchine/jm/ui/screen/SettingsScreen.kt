package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.composable.GradingSelectionRow
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.composable.scaffold.MjuScaffold
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBottomBarItemSelected: (item: NavKey) -> Unit = {},
    settingsState: SettingsViewModel.SettingsViewState = SettingsViewModel.SettingsViewState(),
    feedback: String? = "",
    onShowOnBoardingRequested: () -> Unit = {},
    onPlaySoundChange: (Boolean) -> Unit = {},
    onPinScreenChange: (Boolean) -> Unit = {},
    onDefaultGradingSelected: (Grading) -> Unit = {},
    onDismissFeedback: () -> Unit = {},
) {
    MjuScaffold(
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
        showMenu = true,
        menuItemSelected = Screens.Settings,
        onMenuItemSelected = { destination -> onBottomBarItemSelected(destination) },
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
        Column(
            modifier = Modifier
                .padding(end = Theme.spacing.extraSmall)
                .weight(1.0f),
        ) {
            Text(
                modifier = Modifier
                    .semantics {
                        hideFromAccessibility()
                    },
                text = titleText,
            )
        }
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
            Text(
                text = stringResource(R.string.setting_show_onboarding_button),
            )
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
    val titleText = stringResource(title)
    val labelText = stringResource(label)
    val settingText = stringResource(R.string.tts_setting)
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
            .clearAndSetSemantics {
                contentDescription = "$settingText: $titleText, $labelText"
                role = Role.Switch
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
                onClick(
                    label = if (checked) {
                        deactivateText
                    } else {
                        activateText
                    },
                    action = { true },
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
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}


@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
)
@Composable
fun PreviewSettingsScreen() {
    JmTheme {
        SettingsScreen()
    }
}
