package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.Screens
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    onBrowseSource: () -> Unit = {},
    onReportBug: () -> Unit = {},
    onOpenWebsite: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize().testTag("screen_about"),
        bottomBar = {
            MjuBottomBar(
                selected = navController.currentDestination?.route ?: Screens.About.name,
                onItemSelected = { destination -> navController.navigate(destination.id) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(8.dp),
                onClick = { onReportBug() },
                icon = { Icon(Icons.Filled.Build, "Report") },
                text = { Text(text = stringResource(R.string.button_report_bug)) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(state = ScrollState(initial = 0))
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                text = stringResource(R.string.title_about),
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.about_this_app_is_libre_software),
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.about_help_us_make_it_better),
            )

            Spacer(Modifier.padding(16.dp))

            OutlinedButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                onClick = { onBrowseSource() },
            ) {
                Text(stringResource(R.string.button_browse_the_source))
            }

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                TextButton(
                    onClick = { onOpenWebsite() },
                ) {
                    Text("MieuxVoter.fr")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewAboutScreen(modifier: Modifier = Modifier) {
    JmTheme {
        AboutScreen()
    }
}
