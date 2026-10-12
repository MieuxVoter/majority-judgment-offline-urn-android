package com.illiouchine.jm.ui.composable.title

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun ScreenTitle(
    modifier: Modifier = Modifier,
    text: String = "",
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = Theme.spacing.large.times(other = 2),
            ),
        style = Theme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        text = text,
    )
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewScreenTitle() {
    JmTheme {
        Column {
            ScreenTitle(
                text = "The Best Title",
            )
        }
    }
}
