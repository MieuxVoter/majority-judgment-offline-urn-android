package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.ProportionalAlgorithms
import com.illiouchine.jm.ui.DefaultNavigator
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch


@Composable
fun ProportionsHelpScreen(
    modifier: Modifier = Modifier,
    navigator: Navigator = DefaultNavigator(),
) {

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_proportions_help"),
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
//        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(innerPadding)
                .padding(horizontal = Theme.spacing.small)
        ) {

            ScreenTitle(text = "Proportional\nRepresentation")

            Text(
                text = "The purpose of proportional representation is to share amongst candidates a finite resource such as time, assembly seats, money…",
                textAlign = TextAlign.Justify,
            )

            Spacer(Modifier.padding(vertical = Theme.spacing.small))

            Text(
                text = "There are many algorithms that can compute a proportional representation, each with its own virtues and issues.",
                textAlign = TextAlign.Justify,
            )

            Spacer(Modifier.padding(vertical = Theme.spacing.small))

            Text("Choose wisely.")

            for (algo in ProportionalAlgorithms.entries) {
                if (algo == ProportionalAlgorithms.NONE) continue
                Text(
                    modifier = Modifier.padding(
                        top = Theme.spacing.large,
                        bottom = Theme.spacing.medium
                    ),
                    text = algo.getName(LocalContext.current),
                    fontSize = 24.sp,
                )
                Text(
                    modifier = Modifier,
                    text = algo.getDescription(LocalContext.current),
                    textAlign = TextAlign.Justify,
                )
                Text(
                    modifier = Modifier.padding(top=Theme.spacing.medium),
                    text = algo.getFeatures(LocalContext.current),
                )
            }

            Spacer(Modifier.padding(vertical = Theme.spacing.large))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    coroutineScope.launch {
                        navigator.navigateUp()
                    }
                },
            ) {
                Text(stringResource(R.string.button_back))
            }

            Spacer(Modifier.padding(vertical = Theme.spacing.large))
        }
    }
}


@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewProportionsHelpScreen(modifier: Modifier = Modifier) {
    JmTheme {
        ProportionsHelpScreen(
            modifier = modifier,
        )
    }
}
