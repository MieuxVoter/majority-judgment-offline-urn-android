package com.illiouchine.jm.ui.screen

import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.BallotCountRow
import com.illiouchine.jm.ui.composable.GradeSelectionList
import com.illiouchine.jm.ui.composable.JudgmentBalls
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.launch

@Composable
fun PollVotingScreen(
    modifier: Modifier = Modifier,
    pollVotingState: PollVotingViewModel.PollVotingViewState = PollVotingViewModel.PollVotingViewState(),
    onStartVoting: () -> Unit = {},
    onJudgmentCast: (Judgment) -> Unit = {},
    onBallotConfirmed: (Context, Ballot) -> Unit = { _, _ -> },
    onBallotCanceled: () -> Unit = {},
    onCancelLastJudgment: () -> Unit = {},
    onTryToGoBack: (Context) -> Unit = {},
    onFinish: () -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("voting_screen"),
        snackbarHost = {
            MjuSnackbar(
                modifier = Modifier,
                text = feedback,
                onDismiss = { onDismissFeedback() },
            )
        },
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(16.dp),
        ) {

            PollSubject(
                subject = pollVotingState.pollConfig.subject,
            )

            if (pollVotingState.isInStateReady()) {

                Spacer(modifier = Modifier.height(32.dp))

                // State: READY, waiting for new participant.
                if (pollVotingState.ballots.isNotEmpty()) {
                    Text(stringResource(R.string.help_your_participation_was_a_success))
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (pollVotingState.ballots.isEmpty()) {
                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .testTag("voting_start_first"),
                        onClick = { onStartVoting() },
                        content = {
                            Text(stringResource(R.string.button_be_the_first_to_vote))
                        }
                    )
                } else {
                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .testTag("voting_start_next"),
                        onClick = { onStartVoting() },
                        content = {
                            Text(stringResource(R.string.button_next_participant))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = pollVotingState.ballots.isNotEmpty(),
                    onClick = { onFinish() },
                ) { Text(stringResource(R.string.button_end_the_poll)) }
            } else {

                if (pollVotingState.isInStateVoting()) {

                    val scrollCoroutine = rememberCoroutineScope()

                    // State: VOTING, filling the ballot with judgments.
                    val currentProposalIndex =
                        pollVotingState.currentProposalsOrder[pollVotingState.currentBallot!!.judgments.size]
                    GradeSelectionList(
                        pollConfig = pollVotingState.pollConfig,
                        forProposalIndex = currentProposalIndex,
                        onGradeSelected = { result ->
                            val judgment = Judgment(
                                proposal = currentProposalIndex,
                                grade = result,
                            )
                            onJudgmentCast(judgment)
                            scrollCoroutine.launch {
                                scrollState.scrollTo(67)
                            }
                        }
                    )
                    JudgmentBalls(
                        modifier = Modifier.fillMaxWidth(),
                        pollConfig = pollVotingState.pollConfig,
                        ballot = pollVotingState.currentBallot,
                    )
                } else {

                    // State: SUMMARY, awaiting confirmation, back or redo.
                    BallotSummaryScreen(
                        pollConfig = pollVotingState.pollConfig,
                        ballot = pollVotingState.currentBallot!!,
                        onConfirm = {
                            onBallotConfirmed(context, pollVotingState.currentBallot)
                        },
                        onCancel = {
                            onBallotCanceled()
                        },
                    )
                }
            }


            Spacer(
                modifier = Modifier.padding(12.dp),
            )

            BallotCountRow(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                ballots = pollVotingState.ballots,
            )
        }
    }

    // Rule: going BACK before starting any ballot goes back to the previous screen.
    // Rule: going BACK after starting any ballot goes back to the previous screen.
    // Rule: going BACK cancels the last cast judgment, if any.
    // Rule: going BACK from the summary cancels the last cast judgment too.
    BackHandler(
        enabled = (pollVotingState.amountOfBallotsCastThisSession > 0) || (pollVotingState.currentBallot != null),
    ) {
        if (pollVotingState.currentBallot != null) {
            if (pollVotingState.currentBallot.judgments.isNotEmpty()) {
                onCancelLastJudgment()
            } else {
                onBallotCanceled()
            }
        } else {
            onTryToGoBack(context)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewVotingScreen(modifier: Modifier = Modifier) {
    JmTheme {
        PollVotingScreen(
            modifier = modifier,
            pollVotingState = PollVotingViewModel.PollVotingViewState(
                pollConfig = PollConfig(
                    subject = "Best Prezidan ?",
                    proposals = listOf("That candidate with a long name-san", "Mario", "JanBob"),
                    grading = DEFAULT_GRADING_QUALITY_VALUE,
                ),
                ballots = emptyList(),
                currentBallot = null,
            ),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewVotingScreenWithBallots(modifier: Modifier = Modifier) {
    JmTheme {
        PollVotingScreen(
            modifier = modifier,
            pollVotingState = PollVotingViewModel.PollVotingViewState(
                pollConfig = PollConfig(
                    subject = "Best Prezidan ?",
                    proposals = listOf("That candidate with a long name-san", "Mario", "JanBob"),
                    grading = DEFAULT_GRADING_QUALITY_VALUE,
                ),
                ballots = listOf(
                    Ballot(judgments = listOf(Judgment(1, grade = 3)))
                ),
                currentBallot = null,
            ),
        )
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewVotingScreenConfirmation(modifier: Modifier = Modifier) {
    JmTheme {
        PollVotingScreen(
            modifier = Modifier,
            pollVotingState = PollVotingViewModel.PollVotingViewState(
                pollConfig = PollConfig(
                    subject = "Oh my gawd this poll has a very long title, much wow !!1!",
                    proposals = listOf("That candidate with quite a long name-san", "Mario", "JanBob"),
                    grading = DEFAULT_GRADING_QUALITY_VALUE,
                ),
                ballots = listOf(
                    Ballot(judgments = listOf(Judgment(proposal = 1, grade = 3)))
                ),
                currentBallot = Ballot(
                    judgments = listOf(
                        Judgment(proposal = 0, grade = 0),
                        Judgment(proposal = 1, grade = 3),
                        Judgment(proposal = 2, grade = 2),
                    ),
                ),
            ),
        )
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewVotingScreenWithCurrentBallots(modifier: Modifier = Modifier) {
    JmTheme {
        PollVotingScreen(
            modifier = modifier,
            pollVotingState = PollVotingViewModel.PollVotingViewState(
                pollConfig = PollConfig(
                    subject = "Best Prezidan ?",
                    proposals = listOf(
                        "That candidate with quite a long name-san",
                        "Mario",
                        "JanBob",
                    ),
                    grading = DEFAULT_GRADING_QUALITY_VALUE,
                ),
                ballots = listOf(
                    Ballot(
                        judgments = listOf(
                            Judgment(proposal = 0, grade = 2),
                            Judgment(proposal = 1, grade = 2),
                            Judgment(proposal = 2, grade = 3),
                        )
                    )
                ),
                currentBallot = Ballot(
                    judgments = emptyList(),
                ),
            ),
        )
    }
}
