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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.theme.DeleteColor
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun PollSummary(
    modifier: Modifier = Modifier,
    poll: Poll = Poll(pollConfig = PollConfig(), ballots = emptyList()),
    onSetupClonePoll: (poll: Poll) -> Unit = {},
    onResumePoll: (poll: Poll) -> Unit = {},
    onShowResult: (poll: Poll) -> Unit = {},
    onDeletePoll: (poll: Poll) -> Unit = {},
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            // TalkBack accessibility
            .semantics(
                mergeDescendants = true,
            ) {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = context.getString(R.string.action_inspect),
                        action = {
                            onShowResult(poll)
                            true
                        }
                    ),
                    CustomAccessibilityAction(
                        label = context.getString(R.string.action_resume),
                        action = {
                            onResumePoll(poll)
                            true
                        }
                    ),
                    CustomAccessibilityAction(
                        label = context.getString(R.string.action_clone),
                        action = {
                            onSetupClonePoll(poll)
                            true
                        }
                    ),
                    CustomAccessibilityAction(
                        label = context.getString(R.string.action_delete),
                        action = {
                            onDeletePoll(poll)
                            true
                        }
                    ),
                )
            },
    ) {
        Column {
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
                    modifier = Modifier.weight(1f).padding(top = 4.dp),
                    text = sequenceOfProposals.toString(),
                )

                // homemade pluralStringResource ; NIH
                val votesString = if (poll.ballots.size > 1) {
                    stringResource(R.string.votes)
                } else {
                    stringResource(R.string.vote)
                }
                Text(
                    modifier = Modifier.align(Alignment.Bottom),
                    fontStyle = FontStyle.Italic,
                    text = "(${poll.ballots.size} " + votesString + ")",
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Disable the buttons for TalkBack, we're using accessibility actions instead.
                    .clearAndSetSemantics {},
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TextButton(
                    onClick = { onDeletePoll(poll) },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = DeleteColor,
                    ),
                ) {
                    Text(stringResource(R.string.action_delete))
                }
                TextButton(onClick = { onSetupClonePoll(poll) }) {
                    Text(stringResource(R.string.action_clone))
                }
                TextButton(onClick = { onResumePoll(poll) }) {
                    Text(stringResource(R.string.action_resume))
                }
                TextButton(
                    enabled = poll.ballots.isNotEmpty(),
                    onClick = { onShowResult(poll) },
                ) {
                    Text(stringResource(R.string.action_inspect))
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
