package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.BallotCountRow
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme
import java.math.BigInteger


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

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .verticalScroll(state = ScrollState(initial = 0))
                .padding(8.dp),
        ) {
            PollSubject(
                subject = poll.pollConfig.subject,
            )

            result.proposalResultsRanked.forEach { proposalResult ->
                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
                    val rank = proposalResult.rank
                    val proposalName = poll.pollConfig.proposals[proposalResult.index]
                    val medianGrade = proposalResult.analysis.medianGrade
                    val medianGradeName = stringResource(poll.pollConfig.grading.getGradeName(medianGrade))
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
                    // Draw the linear merit profile of the proposal.
                    val medianLineColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    ) {
                        val proposalTally = tally.proposalsTallies[proposalResult.index]
                        val bigSizeWidth = BigInteger.valueOf(size.width.toLong())
                        var offsetX = 0F

                        for (gradeIndex in 0..<grading.getAmountOfGrades()) {
                            val sizeW = bigSizeWidth.multiply(
                                proposalTally.tally[gradeIndex]
                            ).divide(
                                proposalTally.amountOfJudgments
                            )

                            drawRect(
                                color = grading.getGradeColor(gradeIndex),
                                size = Size(sizeW.toFloat(), size.height),
                                topLeft = Offset(offsetX, 0F)
                            )

                            offsetX += sizeW.toFloat()
                        }

                        // Vertical line in the middle, marking the median grade.
                        drawLine(
                            color = medianLineColor,
                            start = Offset(size.width * 0.5f, -10f),
                            end = Offset(size.width * 0.5f, size.height + 10f),
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(10f, 5f),
                                phase = -0.5f,
                            ),
                            strokeWidth = 4f,
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(12.dp))
            }

            Spacer(modifier = Modifier.padding(8.dp))

            BallotCountRow(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                ballots = poll.ballots,
            )

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
            subject = "Prézidaaanh ?",
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
    val pollResultViewModel = PollResultViewModel()
    pollResultViewModel.finalizePoll(poll)
    val state = pollResultViewModel.pollResultViewState.collectAsState().value
    JmTheme {
        ResultScreen(
            state = state,
        )
    }
}