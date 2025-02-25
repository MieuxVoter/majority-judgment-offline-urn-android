package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.deleteColor

@Composable
fun PollSummary(
    modifier: Modifier = Modifier,
    poll: Poll = Poll(pollConfig = PollConfig(), ballots = emptyList()),
    onSetupClonePoll: (poll: Poll) -> Unit = {},
    onResumePoll: (poll: Poll) -> Unit = {},
    onShowResult: (poll: Poll) -> Unit = {},
    onDeletePoll: (poll: Poll) -> Unit = {},
) {
    Row(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                modifier = Modifier,
                text = poll.pollConfig.subject,
                fontWeight = FontWeight.Bold,
            )
            Row {
                val sequenceOfProposals = StringBuilder()
                poll.pollConfig.proposals.forEachIndexed { proposalIndex, proposal ->
                    if (proposalIndex > 0) {
                        sequenceOfProposals.append(", ")
                    }
                    sequenceOfProposals.append(proposal)
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = sequenceOfProposals.toString(),
                )

                Text(
                    modifier = Modifier.align(Alignment.Bottom),
                    fontStyle = FontStyle.Italic,
                    text = "(${poll.ballots.size} votes)",
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = { onDeletePoll(poll) },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = deleteColor
                    )
                ) {
                    Text("Delete")
                }
                TextButton(onClick = { onSetupClonePoll(poll) }) {
                    Text("Clone")
                }
                TextButton(onClick = { onResumePoll(poll) }) {
                    Text("Resume")
                }
                TextButton(onClick = { onShowResult(poll) }) {
                    Text("Inspect")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPollSummary() {
    JmTheme {
        PollSummary(
            poll = Poll(
                pollConfig = PollConfig(
                    subject = "Poll Subject",
                    proposals = listOf("proposal 1", "proposal 2")
                ),
                ballots = emptyList()
            )
        )
    }
}