package com.illiouchine.jm.ui.screen

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.JudgmentSummary
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.DeleteColor


@Composable
fun VoteSummaryScreen(
    modifier: Modifier = Modifier,
    pollConfig: PollConfig,
    ballot: Ballot,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.summary_are_you_sure),
            fontSize = 8.em,
        )

        Spacer(Modifier.height(32.dp))

        ballot.judgments.forEach { judgment ->
            val gradeIndex = judgment.grade
            val proposal = pollConfig.proposals[judgment.proposal]
            JudgmentSummary(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                proposal = proposal,
                gradeString = stringResource(pollConfig.grading.getGradeName(gradeIndex)),
                color = pollConfig.grading.getGradeColor(gradeIndex)
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.summary_is_that_accurate),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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


@Preview(showSystemUi = true)
@Composable
fun PreviewVoteSummaryScreen(modifier: Modifier = Modifier) {
    JmTheme {
        VoteSummaryScreen(
            pollConfig = PollConfig(
                subject = "Prézidaaanh ?",
                proposals = listOf("Tonio", "Bobby", "Mario"),
                grading = Grading.Quality7Grading,
            ),
            ballot = Ballot(
                judgments = listOf(
                    Judgment(0, 6),
                    Judgment(1, 1),
                    Judgment(2, 5),
                )
            ),
        )
    }
}