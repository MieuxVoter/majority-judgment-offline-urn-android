package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun JudgmentBalls(
    modifier: Modifier = Modifier,
    pollConfig: PollConfig = PollConfig(),
    ballot: Ballot = Ballot(),
) {
    val currentBalls = ballot.judgments.size
    Row(
        modifier = modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until pollConfig.proposals.size) {
            val color = when {
                i < currentBalls -> pollConfig.grading.getGradeColor(ballot.judgments[i].grade)
                i == currentBalls -> Color.LightGray
                i > currentBalls -> Color.DarkGray
                else -> Color.Blue
            }
            val size = if (i == currentBalls) {
                24.dp
            } else {
                16.dp
            }
            Spacer(modifier = Modifier.size(4.dp))
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewJudgmentBall() {
    JmTheme {
        JudgmentBalls(
            pollConfig = PollConfig(proposals = listOf("a", "b", "c", "d")),
            ballot = Ballot(
                judgments = listOf(
                    Judgment(proposal = 1, grade = 1),
                    Judgment(proposal = 2, grade = 3),
                    Judgment(proposal = 3, grade = 6),
                    Judgment(proposal = 4, grade = 2),
                )
            )
        )
    }
}

@Preview
@Composable
private fun PreviewJudgmentBallEmpty() {
    JmTheme {
        JudgmentBalls(
            pollConfig = PollConfig(proposals = listOf("a", "b", "c", "d")),
            ballot = Ballot(
                judgments = emptyList()
            )
        )
    }
}

@Preview
@Composable
private fun PreviewJudgmentBallMidle() {
    JmTheme {
        JudgmentBalls(
            pollConfig = PollConfig(proposals = listOf("a", "b", "c", "d")),
            ballot = Ballot(
                judgments = listOf(
                    Judgment(proposal = 1, grade = 1),
                    Judgment(proposal = 2, grade = 3),
                )
            )
        )
    }
}