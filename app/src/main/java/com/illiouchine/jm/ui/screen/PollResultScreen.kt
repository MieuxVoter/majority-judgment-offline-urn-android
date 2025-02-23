package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.ui.composable.MUSnackbar
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
    val grading = poll.pollConfig.grading
    val amountOfProposals = poll.pollConfig.proposals.size
    val amountOfGrades = poll.pollConfig.grading.getAmountOfGrades()
    val deliberation: DeliberatorInterface = MajorityJudgmentDeliberator()
    val tally = CollectedTally(amountOfProposals, amountOfGrades)

    poll.pollConfig.proposals.forEachIndexed { i, prop ->
        val voteResult = poll.judgments.filter { it.proposal == prop }
        voteResult.forEach { judgment ->
            tally.collect(i, judgment.grade)
        }
    }

    val result: ResultInterface = deliberation.deliberate(tally)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            MUSnackbar(
                modifier = Modifier,
                text = feedback,
                onDismiss = {
                    onDismissFeedback()
                },
            )
        },
        // TODO: figure out the weird gap "bug" that this generates
//                    bottomBar = {
//                        MUBottomBar(
//                            modifier = Modifier,
//                            selected = navController.currentDestination?.route ?: "home",
//                            onItemSelected = { destination -> navController.navigate(destination.id) }
//                        )
//                    },
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(state = ScrollState(initial = 0))
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    modifier = modifier.padding(32.dp),
                    text = "❝ ${poll.pollConfig.subject} ❞",
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

                Row(modifier = Modifier.padding(8.dp)) {
                    // cheap gap between proposals
                }

            }


            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onFinish,
            ) { Text("Finish") }
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
        ResultScreen(
            poll = poll,
        )
    }
}