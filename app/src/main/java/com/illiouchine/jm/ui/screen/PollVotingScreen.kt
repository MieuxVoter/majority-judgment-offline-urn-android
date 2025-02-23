package com.illiouchine.jm.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.PollVotingViewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PollVotingScreen(
    modifier: Modifier = Modifier,
    pollVotingState: PollVotingViewModel.PollVotingViewState = PollVotingViewModel.PollVotingViewState(),
    onStartVoting: () -> Unit = {},
    onJudgmentCast: (Judgment) -> Unit = {},
    onBallotConfirmed: (Ballot) -> Unit = {},
    onBallotCanceled: () -> Unit = {},
    onCancelLastJudgment: () -> Unit = {},
    onFinish: (Poll) -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            MjuSnackbar(
                modifier = Modifier,
                text = feedback,
                onDismiss = { onDismissFeedback() },
            )
        },
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(state = ScrollState(initial = 0))
                .padding(16.dp),
        ) {

            PollSubject(
                subject = pollVotingState.pollConfig.subject,
            )

            if (pollVotingState.isInStateReady()) {

                // State: READY, waiting for new participant.
                if (pollVotingState.ballots.isNotEmpty()) {
                    Text("Votre participation a bien été prise en compte.\nVous pouvez maintenant passer cet appareil au prochain participant.")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        enabled = pollVotingState.ballots.isNotEmpty(),
                        onClick = {
                            val poll = Poll(
                                pollConfig = pollVotingState.pollConfig,
                                ballots = pollVotingState.ballots
                            )
                            onFinish(poll)
                        }
                    ) { Text(stringResource(R.string.button_end_the_poll)) }
                    if (pollVotingState.ballots.isEmpty()) {
                        Button(
                            onClick = { onStartVoting() },
                            content = {
                                Text("Be the first to vote")
                            }
                        )
                    } else {
                        Button(
                            onClick = { onStartVoting() },
                            content = {
                                Text(stringResource(R.string.button_next_participant))
                            }
                        )
                    }
                }
            } else {

                val currentProposalIndex = pollVotingState.currentBallot!!.judgments.size

                if (pollVotingState.isInStateVoting()) {

                    // State: VOTING, filling the ballot with judgments.
                    GradeSelection(
                        pollConfig = pollVotingState.pollConfig,
                        forProposalIndex = currentProposalIndex,
                        onGradeSelected = { result ->
                            val judgment = Judgment(
                                proposal = pollVotingState.pollConfig.proposals[currentProposalIndex],
                                grade = result,
                            )
                            onJudgmentCast(judgment)
                        }
                    )

                } else {

                    // State: SUMMARY, awaiting confirmation, back or redo.
                    VoteSummaryScreen(
                        pollConfig = pollVotingState.pollConfig,
                        ballot = pollVotingState.currentBallot,
                        onConfirm = {
                            onBallotConfirmed(pollVotingState.currentBallot)
                        },
                        onCancel = {
                            onBallotCanceled()
                        },
                    )
                }
            }


            val amountOfBallots = pollVotingState.ballots.size
            Spacer(
                modifier = Modifier.padding(12.dp),
            )
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                val ballotsString = if (amountOfBallots <= 1)
                    stringResource(R.string.ballot)
                else
                    stringResource(R.string.ballots)

                Text(
                    "${amountOfBallots} ${ballotsString} " + stringResource(R.string.in_the_urn)
                )
            }
        }
    }

    // Rule: going BACK cancels the last cast judgment, if any.
    // Rule: going BACK from the summary cancels the last cast judgment too.
    BackHandler(
        enabled = true,
    ) {
        if (pollVotingState.currentBallot != null) {
            if (pollVotingState.currentBallot.judgments.isNotEmpty()) {
                onCancelLastJudgment()
            }
        }
    }
}


@Composable
private fun GradeSelection(
    pollConfig: PollConfig,
    forProposalIndex: Int,
    onGradeSelected: (Int) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.all_things_considered_i_think),
            fontStyle = FontStyle.Italic,
        )
    }
    // Using Row here instead won't center the text on the phone, even though it does in the preview
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = pollConfig.proposals[forProposalIndex],
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            fontSize = 8.em,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.verb_is),
            fontStyle = FontStyle.Italic,
        )
    }

    val context = LocalContext.current
    var selectedGradeIndex: Int? by remember { mutableStateOf(null) }

    for (gradeIndex in 0..<pollConfig.grading.getAmountOfGrades()) {
        val bgColor = pollConfig.grading.getGradeColor(gradeIndex)
        val fgColor = pollConfig.grading.getGradeTextColor(gradeIndex)

        val interactionSource = remember { MutableInteractionSource() }
        val interactionSourceIsPressed by interactionSource.collectIsFocusedAsState()
        val coroutine = rememberCoroutineScope()

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (interactionSourceIsPressed) 80.dp else 64.dp)
                .padding(top = 12.dp),

            enabled = (selectedGradeIndex == null) || (selectedGradeIndex == gradeIndex),

            onClick = {
                if (selectedGradeIndex != null) {
                    return@Button
                }
                selectedGradeIndex = gradeIndex
                coroutine.launch {
                    delay(150)
                    onGradeSelected(gradeIndex)
                    selectedGradeIndex = null
                }
            },

            colors = ButtonColors(
                containerColor = bgColor,
                contentColor = fgColor,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White,
            ),
        ) {
            Text(
                text = context.getString(pollConfig.grading.getGradeName(gradeIndex)).uppercase(),
                fontSize = 5.em,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewVotingScreen(modifier: Modifier = Modifier) {
    JmTheme {
        PollVotingScreen(
            modifier = Modifier,
            pollVotingState = PollVotingViewModel.PollVotingViewState(
                pollConfig = PollConfig(
                    subject = "Best Prezidan ?",
                    proposals = listOf("That candidate with a long name-san", "Mario", "JanBob"),
                    grading = Quality7Grading(),
                ),
                ballots = emptyList(),
                currentBallot = null,
            ),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewVotingScreenWithBallots(modifier: Modifier = Modifier) {
    JmTheme {
        PollVotingScreen(
            modifier = Modifier,
            pollVotingState = PollVotingViewModel.PollVotingViewState(
                pollConfig = PollConfig(
                    subject = "Best Prezidan ?",
                    proposals = listOf("That candidate with a long name-san", "Mario", "JanBob"),
                    grading = Quality7Grading(),
                ),
                ballots = listOf(
                    Ballot(judgments = listOf(Judgment("Mario", grade = 3)))
                ),
                currentBallot = null,
            ),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewVotingScreenWithCurrentBallots(modifier: Modifier = Modifier) {
    JmTheme {
        PollVotingScreen(
            modifier = Modifier,
            pollVotingState = PollVotingViewModel.PollVotingViewState(
                pollConfig = PollConfig(
                    subject = "Best Prezidan ?",
                    proposals = listOf("That candidate with a long name-san", "Mario", "JanBob"),
                    grading = Quality7Grading(),
                ),
                ballots = listOf(
                    Ballot(
                        judgments = listOf(
                            Judgment("That candidate with a long name-san", grade = 2),
                            Judgment("Mario", grade = 6),
                            Judgment("JanBob", grade = 3),
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