package com.illiouchine.jm.ui.screen.help

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.composable.MarkdownText
import com.illiouchine.jm.ui.composable.spacer.MediumVerticalSpacer
import com.illiouchine.jm.ui.composable.title.ScreenTitle
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun LotteryHelpScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_lottery_help"),
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
//        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(paddingValues = innerPadding)
                .padding(horizontal = Theme.spacing.small)
        ) {
            ScreenTitle(
                text = stringResource(R.string.lottery),
            )

            MarkdownText(
                modifier = Modifier.fillMaxWidth(),
                // TODO: add this to translations once we're OK with it
                text = """
This app can use the proportions from the chosen proportional algorithm to draw a weighted lottery.

Weighing the lottery means that the higher the proportion a proposal gets, the higher its chances of being drawn are.

Such a lottery is especially useful for small, repeated group choices.

_For example, when choosing a restaurant, it helps the vegan minority not to get outvoted every time._ 

People tend to be flexible about what they like, but much less flexible about what they don't like. A lottery that might pick something several people strongly object to won't feel fair, even if the odds were proportional.

To avoid such pitfalls, you are encouraged to collegially define beforehand one or more _acceptation thresholds_, such as:

✅ a minimum median grade (such as _Passable_)
✅ a maximum share of _Rejection_ grades, which works as a soft veto
                """.trimIndent(),
                style = Theme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                // textAlign = TextAlign.Justify, // Y U NO WORK OMG
            )

            MediumVerticalSpacer()

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    coroutineScope.launch {
                        onNavigateBack()
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
fun PreviewLotteryHelpScreen(modifier: Modifier = Modifier) {
    JmTheme {
        LotteryHelpScreen(
            modifier = modifier,
        )
    }
}
