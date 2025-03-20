package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.composable.IconTextButton
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.ScreenTitle
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
                selected = Screens.About,
                onItemSelected = { destination -> navController.navigate(destination) },
            )
        },
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(innerPadding)
        ) {
            val uriHandler = LocalUriHandler.current

            ScreenTitle(text = stringResource(R.string.title_about))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier.pointerInput(Unit){
                        detectTapGestures {
                            navController.navigate(Screens.Loader)
                        }
                    },
                    painter = painterResource(R.drawable.onboarding_3),
                    contentDescription = null,
                )
            }

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
                onClick = {
                    onDiscuss()
                    uriHandler.openUri("https://github.com/MieuxVoter/majority-judgment-offline-urn-android/discussions")
                },
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Build,
                text = stringResource(R.string.button_browse_the_source),
                onClick = {
                    onBrowseSource()
                    uriHandler.openUri("https://github.com/MieuxVoter/majority-judgment-offline-urn-android")
                },
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Notifications,
                text = stringResource(R.string.button_report_bug),
                onClick = {
                    onReportBug()
                    uriHandler.openUri("https://github.com/MieuxVoter/majority-judgment-offline-urn-android/issues/new?template=bug_report.md")
                },
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Done,
                text = stringResource(R.string.button_suggest_improvement),
                onClick = {
                    onSuggestImprovement()
                    uriHandler.openUri("https://github.com/MieuxVoter/majority-judgment-offline-urn-android/issues/new?template=feature_request.md")
                },
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.LocationOn,
                text = stringResource(R.string.button_translate_app),
                onClick = {
                    onContributeTranslations()
                    uriHandler.openUri("https://github.com/MieuxVoter/majority-judgment-offline-urn-android/wiki/How-to-Translate-the-App")
                },
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.Info,
                text = stringResource(R.string.button_more_about_majority_judgment),
                onClick = {
                    onOpenWebsite()
                    uriHandler.openUri("https://mieuxvoter.fr")
                },
            )

            IconTextButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                icon = Icons.Filled.ThumbUp,
                text = stringResource(R.string.button_try_online_webapp),
                onClick = {
                    uriHandler.openUri("https://app.mieuxvoter.fr/")
                },
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
