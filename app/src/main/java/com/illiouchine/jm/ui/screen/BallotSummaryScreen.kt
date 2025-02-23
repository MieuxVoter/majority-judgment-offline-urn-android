package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun VoteSummaryScreen(
    modifier: Modifier = Modifier,
    poll: Poll,
    ballot: Ballot,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
//            .verticalScroll(state = ScrollState(initial = 0))
//            .padding(8.dp)
        ,
    ) {

        Text(
            text = "Are you sure ?",
            fontSize = 8.em,
        )
        Text(
            text = "Here's a summary of your judgments :",
        )

        Spacer(Modifier.height(32.dp))

        poll.pollConfig.proposals.forEachIndexed { proposalIndex, proposal ->
            val grade = ballot.judgments[proposalIndex].grade

            Row {
                Text(proposal)
                Text(" is ")
                Text(stringResource(poll.pollConfig.grading.getGradeName(grade)))
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Is that accurate ?",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {

            OutlinedButton(
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
    val poll = Poll(
        pollConfig = PollConfig(
            subject = "Prézidaaanh ?",
            proposals = listOf("Tonio", "Bobby", "Mario"),
            grading = Quality7Grading(),
        ),
        ballots = listOf(
            Ballot(
                judgments = listOf(
                    Judgment("Tonio", 0),
                    Judgment("Bobby", 5),
                    Judgment("Mario", 6),
                )
            ),
            Ballot(
                judgments = listOf(
                    Judgment("Tonio", 4),
                    Judgment("Bobby", 1),
                    Judgment("Mario", 6),
                )
            ),
            Ballot(
                judgments = listOf(
                    Judgment("Tonio", 5),
                    Judgment("Bobby", 5),
                    Judgment("Mario", 6),
                )
            ),
        ),
    )
    JmTheme {
        VoteSummaryScreen(
            poll = poll,
            ballot = Ballot(
                judgments = listOf(
                    Judgment("Tonio", 6),
                    Judgment("Bobby", 1),
                    Judgment("Mario", 5),
                )
            ),
        )
    }
}