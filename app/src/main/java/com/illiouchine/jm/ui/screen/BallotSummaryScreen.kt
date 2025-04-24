package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.JudgmentSummary
import com.illiouchine.jm.ui.theme.DeleteColor
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing


@Composable
fun BallotSummaryScreen(
    modifier: Modifier = Modifier,
    pollConfig: PollConfig,
    ballot: Ballot,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Theme.spacing.medium),
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.summary_are_you_sure),
            fontSize = 32.sp,
        )

        Spacer(Modifier.height(Theme.spacing.large))

        ballot.judgments.forEach { judgment ->
            val gradeIndex = judgment.grade
            val proposal = pollConfig.proposals[judgment.proposal]
            JudgmentSummary(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.extraSmall),
                proposal = proposal,
                gradeString = stringResource(pollConfig.grading.getGradeName(gradeIndex)),
                color = pollConfig.grading.getGradeColor(gradeIndex),
            )
            Spacer(Modifier.height(Theme.spacing.small))
        }

        Spacer(Modifier.height(Theme.spacing.large))

        Text(
            text = stringResource(R.string.summary_is_that_accurate),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Theme.spacing.medium),
        ) {

            TextButton(
                onClick = onCancel,
            ) {
                Text(
                    text = stringResource(R.string.summary_button_no_rescind),
                    color = DeleteColor,
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onConfirm,
            ) {
                Text(
                    text = stringResource(R.string.summary_button_yes_confirm),
                )
            }
        }

    }
}


@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewBallotSummaryScreen(modifier: Modifier = Modifier) {
    JmTheme {
        BallotSummaryScreen(
            modifier = modifier,
            pollConfig = PollConfig(
                subject = "Emperor ?",
                proposals = listOf("Dominus", "Dump", "Dive"),
                grading = DEFAULT_GRADING_QUALITY_VALUE,
            ),
            ballot = Ballot(
                judgments = listOf(
                    Judgment(0, 2),
                    Judgment(1, 1),
                    Judgment(2, 0),
                )
            ),
        )
    }
}
