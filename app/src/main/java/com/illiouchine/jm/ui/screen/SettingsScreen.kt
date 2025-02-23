package com.illiouchine.jm.ui.screen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.Screens
import com.illiouchine.jm.SettingsViewModel
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
                selected = navController.currentDestination?.route ?: Screens.Settings.name,
                onItemSelected = { destination -> navController.navigate(destination.id) }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Settings")
            var showOnBoardingCheck by remember { mutableStateOf(settingsState.showOnboarding) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Show on boarding")
                Switch(
                    modifier = Modifier,
                    checked = showOnBoardingCheck,
                    onCheckedChange = {
                        showOnBoardingCheck = it
                        onShowOnboardingChange(it)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
    JmTheme {
        SettingsScreen()
    }
}