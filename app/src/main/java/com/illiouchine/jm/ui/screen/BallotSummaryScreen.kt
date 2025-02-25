package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.ui.composable.JudgmentSummary
import com.illiouchine.jm.ui.theme.JmTheme


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
            text = "Are you sure ?",
            fontSize = 8.em,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Here's a summary of your judgments :",
        )

        Spacer(Modifier.height(32.dp))

        pollConfig.proposals.forEachIndexed { proposalIndex, proposal ->
            val grade = ballot.judgments[proposalIndex].grade
            JudgmentSummary(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                proposal = proposal,
                gradeString = stringResource(pollConfig.grading.getGradeName(grade)),
                color = pollConfig.grading.getGradeColor(grade)
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Is that accurate ?",
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
        ) {

            TextButton(
                onClick = onCancel,
            ) {
                Text(
                    text = "No, rescind",
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onConfirm,
            ) {
                Text(
                    text = "Yes, confirm",
                )
            }
        }

    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewVoteSummaryScreen(modifier: Modifier = Modifier) {
    // FIXME: figure out how to reuse this across previews
    // I looked into data providers, but WOW it's complicated >.<
    JmTheme {
        VoteSummaryScreen(
            pollConfig = PollConfig(
                subject = "Prézidaaanh ?",
                proposals = listOf("Tonio", "Bobby", "Mario"),
                grading = Quality7Grading(),
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