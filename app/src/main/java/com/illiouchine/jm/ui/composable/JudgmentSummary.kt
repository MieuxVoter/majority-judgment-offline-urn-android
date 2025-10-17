package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun JudgmentSummary(
    modifier: Modifier = Modifier,
    proposalName: String = "Tonio",
    gradeString: String = "excellent",
    color: Color = Color.Green,
) {
    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JudgmentBall(
            modifier = Modifier.padding(Theme.spacing.small),
            color = color,
        )

        // NOPE: This is quite glitchy on tiny screens
//        Text(
//            modifier = Modifier.weight(1.0f, fill = false),
//            textAlign = TextAlign.Center,
//            text = proposalName,
//        )
//        Text(" " + stringResource(R.string.verb_is) + " ")
//        Text(gradeString)

        Text(
            text = proposalName + " " + stringResource(R.string.verb_is) + " " + gradeString,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewJudgmentSummary() {
    JmTheme {
        JudgmentSummary(
            modifier = Modifier,
            proposalName = "Tonio",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewJudgmentSummaryLongName() {
    JmTheme {
        JudgmentSummary(
            modifier = Modifier,
            proposalName = "That candidate with a long name, so long it eats the end of the sentence",
            gradeString = "somewhat good",
        )
    }
}
