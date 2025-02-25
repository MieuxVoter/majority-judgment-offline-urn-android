package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun JudgmentSummary(
    modifier: Modifier = Modifier,
    proposal: String = "Tonio",
    gradeString: String = "Excellent",
    color: Color = Color.Green
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JudgmentBall(
            modifier = Modifier.padding(8.dp),
            color = color
        )
        Text(proposal)
        Text(" is ")
        Text(gradeString)
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