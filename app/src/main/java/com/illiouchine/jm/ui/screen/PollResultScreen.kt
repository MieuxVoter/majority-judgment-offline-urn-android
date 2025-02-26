package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.theme.JmTheme
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ResultInterface
import java.math.BigInteger


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    poll: Poll,
    onFinish: () -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    // FIXME: refactor this into the viewmodel
    val grading = poll.pollConfig.grading
    val amountOfProposals = poll.pollConfig.proposals.size
    val amountOfGrades = poll.pollConfig.grading.getAmountOfGrades()
    val deliberation: DeliberatorInterface = MajorityJudgmentDeliberator()
    val tally = CollectedTally(amountOfProposals, amountOfGrades)

    poll.pollConfig.proposals.forEachIndexed { proposalIndex, _ ->
        val voteResult = poll.judgments.filter { it.proposal == proposalIndex }
        voteResult.forEach { judgment ->
            tally.collect(proposalIndex, judgment.grade)
        }
    }

    val result: ResultInterface = deliberation.deliberate(tally)

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
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    modifier = modifier.padding(32.dp),
                    text = "❝ ${poll.pollConfig.subject} ❞",
                    fontSize = 6.em,
                )
            }

            result.proposalResultsRanked.forEach { proposalResult ->
                Row {
                    val rank = proposalResult.rank
                    val proposalName = poll.pollConfig.proposals[proposalResult.index]
                    Text("#$rank  $proposalName")
                }

                Row {
                    // Draw the linear merit profile of the proposal.
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

                    }

                }

                Spacer(modifier = Modifier.padding(8.dp))
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
            subject = "Prézidaaanh ?",
            proposals = listOf("Tonio", "Bobby", "Mario"),
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
    JmTheme {
        ResultScreen(
            poll = poll,
        )
    }
}