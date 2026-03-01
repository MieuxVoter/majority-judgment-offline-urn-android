package com.illiouchine.jm.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.preview.PreviewDataBuilder
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun JudgmentBalls(
    modifier: Modifier = Modifier,
    pollConfig: PollConfig = PollConfig(),
    ballot: Ballot = Ballot(),
) {
    val currentBalls = ballot.judgments.size
    Row(
        modifier = modifier
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until pollConfig.proposals.size) {
            val animatedColor by animateColorAsState(
                targetValue = when {
                    i < currentBalls -> pollConfig.grading.getGradeColor(ballot.judgments[i].grade)
                    i == currentBalls -> Color.LightGray
                    i > currentBalls -> Color.DarkGray
                    else -> Color.Blue
                }
            )
            val animatedSize by animateDpAsState(
                targetValue = if (i == currentBalls) {
                    24.dp
                } else {
                    16.dp
                }
            )
            Spacer(modifier = Modifier.size(Theme.spacing.extraSmall))
            JudgmentBall(
                modifier = Modifier,
                color = animatedColor,
                size = animatedSize,
            )
            Spacer(modifier = Modifier.size(Theme.spacing.extraSmall))
        }
    }
}

@Composable
fun JudgmentBall(
    modifier: Modifier = Modifier,
    color: Color = Color.Blue,
    size: Dp = 14.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape = RoundedCornerShape(14.dp))
            .background(color)
    )
}

@Preview
@Composable
private fun PreviewJudgmentBallFull() {
    JmTheme {
        JudgmentBalls(
            pollConfig = PollConfig(proposals = listOf("a", "b", "c", "d")),
            ballot = Ballot(
                judgments = PreviewDataBuilder.judgments(size = 4)
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
                judgments = PreviewDataBuilder.judgments(0)
            )
        )
    }
}

@Preview
@Composable
private fun PreviewJudgmentBallMiddle() {
    JmTheme {
        JudgmentBalls(
            pollConfig = PollConfig(proposals = listOf("a", "b", "c", "d")),
            ballot = Ballot(
                judgments = PreviewDataBuilder.judgments(2)
            )
        )
    }
}
