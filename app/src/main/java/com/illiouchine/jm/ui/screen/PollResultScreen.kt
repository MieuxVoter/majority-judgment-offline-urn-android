package com.illiouchine.jm.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.math.MathUtils.clamp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.composable.BallotCountRow
import com.illiouchine.jm.ui.composable.LinearMeritProfileCanvas
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme
import kotlin.math.max
import kotlin.math.min


// This was not invented here, most definitely.  Where are the steps in Kotlin?
fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
    val value = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f)
    return value * value * (3.0f - 2.0f * value)
}

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    state: PollResultViewModel.PollResultViewState,
    onFinish: () -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    val poll = state.poll!!
    val result = state.result!!
    val tally = state.tally!!
    val grading = poll.pollConfig.grading

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            MjuSnackbar(
                modifier = Modifier,
                text = feedback,
                onDismiss = {
                    onDismissFeedback()
                },
            )
        },
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(8.dp),
        ) {
            PollSubject(
                subject = poll.pollConfig.subject,
            )

            BallotCountRow(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                ballots = poll.ballots,
            )

            Spacer(Modifier.padding(vertical = 8.dp))

            val appearAnimation = remember { Animatable(0f) }
            LaunchedEffect("waterfall") {
                appearAnimation.animateTo(1f, tween(1500))
            }

            val amountOfProposals = result.proposalResultsRanked.size
            result.proposalResultsRanked.forEachIndexed { displayIndex, proposalResult ->
                Column(
                    modifier = Modifier
                        .alpha(
                            smoothStep(
                                max(0f, 0.85f * displayIndex / amountOfProposals),
                                min(1f, 1.15f * (displayIndex + 1) / amountOfProposals),
                                appearAnimation.value,
                            )
                        ),
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        val rank = proposalResult.rank
                        val proposalName = poll.pollConfig.proposals[proposalResult.index]
                        val medianGrade = proposalResult.analysis.medianGrade
                        val medianGradeName =
                            stringResource(poll.pollConfig.grading.getGradeName(medianGrade))
                        Text(
                            modifier = Modifier.padding(end = 12.dp),
                            fontSize = 24.sp,
                            text = "#$rank",
                        )
                        Text(
                            text = "$proposalName   ($medianGradeName)",
                        )
                    }

                    Row {
                        LinearMeritProfileCanvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp),
                            tally = tally,
                            proposalResult = proposalResult,
                            grading = grading,
                        )
                    }

                    Spacer(Modifier.padding(vertical = 12.dp))
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onFinish,
            ) { Text(stringResource(R.string.button_finish)) }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewResultScreen(modifier: Modifier = Modifier) {
    val poll = Poll(
        pollConfig = PollConfig(
            subject = "Who for Prézidaaanh ?",
            proposals = listOf(
                "Luigi the green plumber with a mustache and a long name",
                "Bobby",
                "Mario",
            ),
            grading = Grading.Quality7Grading,
        ),
        ballots = listOf(
            Ballot(
                judgments = listOf(
                    Judgment(0, 0),
                    Judgment(1, 5),
                    Judgment(2, 6),
                )
            ),
            Ballot(
                judgments = listOf(
                    Judgment(0, 4),
                    Judgment(1, 1),
                    Judgment(2, 6),
                )
            ),
            Ballot(
                judgments = listOf(
                    Judgment(0, 5),
                    Judgment(1, 5),
                    Judgment(2, 6),
                )
            ),
        ),
    )
    val pollResultViewModel = PollResultViewModel(Navigator())
    pollResultViewModel.initializePollResult(poll)
    val state = pollResultViewModel.pollResultViewState.collectAsState().value
    JmTheme {
        ResultScreen(
            state = state,
        )
    }
}