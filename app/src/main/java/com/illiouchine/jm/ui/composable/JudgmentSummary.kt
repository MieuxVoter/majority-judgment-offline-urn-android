package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun JudgmentSummary(
    modifier: Modifier = Modifier,
    proposal: String = "Tonio",
    gradeString: String = "Excellent",
    color: Color = Color.Green,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JudgmentBall(
            modifier = Modifier.padding(8.dp),
            color = color,
        )
        Text(
            modifier = Modifier.weight(1.0f, fill = false),
            textAlign = TextAlign.Center,
            text = proposal,
        )
        Text(
            text = " " + stringResource(R.string.verb_is) + " ",
        )
        Text(
            text = gradeString,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewJudgmentSummary() {
    JmTheme {
        JudgmentSummary(
            modifier = Modifier,
            proposal = "Tonio",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewJudgmentSummaryLongName() {
    JmTheme {
        JudgmentSummary(
            modifier = Modifier,
            proposal = "That candidate with a long name, so long it eats the end of the sentence",
        )
    }
}