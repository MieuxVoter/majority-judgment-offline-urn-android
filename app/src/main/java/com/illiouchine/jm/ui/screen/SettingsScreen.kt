package com.illiouchine.jm.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.composable.GradingSelectionRow
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    settingsState: SettingsViewModel.SettingsViewState = SettingsViewModel.SettingsViewState(),
    feedback: String? = "",
    onShowOnboardingChange: (Boolean) -> Unit = {},
    onPlaySoundChange: (Boolean) -> Unit = {},
    onDefaultGradingSelected: (Grading) -> Unit = {},
    onDismissFeedback: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                text = stringResource(R.string.settings_screen_title)
            )

            // TODO: refacto using SwitchSetting
            var showOnBoardingCheck by remember { mutableStateOf(settingsState.showOnboarding) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.show_on_boarding))
                Switch(
                    modifier = Modifier,
                    checked = showOnBoardingCheck,
                    onCheckedChange = {
                        showOnBoardingCheck = it
                        onShowOnboardingChange(it)
                    },
                )
            }

            SwitchSetting(
                title = R.string.setting_play_sound,
                checked = settingsState.playSound,
                onCheckedChange = {
                    onPlaySoundChange(it)
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
fun SwitchSetting(
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