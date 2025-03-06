package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.Screens
import com.illiouchine.jm.ui.composable.IconTextButton
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    onDiscuss: () -> Unit = {},
    onBrowseSource: () -> Unit = {},
    onReportBug: () -> Unit = {},
    onSuggestImprovement: () -> Unit = {},
    onContributeTranslations: () -> Unit = {},
    onOpenWebsite: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_about"),
        bottomBar = {
            MjuBottomBar(
                selected = navController.currentDestination?.route ?: Screens.About.name,
                onItemSelected = { destination -> navController.navigate(destination.id) },
            )
        },
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
                    .padding(bottom = 8.dp),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                text = stringResource(R.string.title_about),
            )

            Image(painterResource(R.drawable.onboarding_3), "")

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

            Spacer(Modifier.padding(8.dp))

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Person,
                text = stringResource(R.string.button_ask_a_question),
                onClick = onDiscuss,
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Build,
                text = stringResource(R.string.button_browse_the_source),
                onClick = onBrowseSource,
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Notifications,
                text = stringResource(R.string.button_report_bug),
                onClick = onReportBug,
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Done,
                text = stringResource(R.string.button_suggest_improvement),
                onClick = onSuggestImprovement,
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.LocationOn,
                text = stringResource(R.string.button_translate_app),
                onClick = onContributeTranslations,
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Info,
                text = stringResource(R.string.button_more_about_majority_judgment),
                onClick = onOpenWebsite,
            )
        }
    }
}


@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewAboutScreen(modifier: Modifier = Modifier) {
    JmTheme {
        AboutScreen()
    }
}
