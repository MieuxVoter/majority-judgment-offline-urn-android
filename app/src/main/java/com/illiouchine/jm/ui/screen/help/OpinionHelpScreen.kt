package com.illiouchine.jm.ui.screen.help

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.composable.MarkdownText
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme

@Composable
fun OpinionHelpScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
) {
    GenericHelpScreen(
        modifier = modifier
            .testTag("screen_opinion_help"),
        title = stringResource(R.string.opinion_profile),
        onNavigateBack = onNavigateBack,
    ) {
        MarkdownText(
            modifier = Modifier.fillMaxWidth(),
            // TODO: add this to translations once we're OK with it
            text = """
The opinion profile shows the global usage of the grades across all candidates.

One can think of it as the _merit profile of the poll_ itself.

For example, a disproportionate amount of rejection grades can mean that the pool of candidates was poorly chosen.

A disproportionate amount of extreme grades can mean that the opinion is quite polarized.
            """.trimIndent(),
            style = Theme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewOpinionHelpScreen(modifier: Modifier = Modifier) {
    JmTheme {
        OpinionHelpScreen(
            modifier = modifier,
        )
    }
}
