package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.model.Grades
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.ui.theme.JmTheme
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ResultInterface


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    surveyResult: SurveyResult,
    onFinish: () -> Unit = {}
) {

    val grading = Quality7Grading() // FIXME: this should be in the poll's parameters
    val amountOfProposals = surveyResult.proposals.size
    val amountOfGrades = Grades.entries.size
    val deliberation: DeliberatorInterface = MajorityJudgmentDeliberator()
    val tally = CollectedTally(amountOfProposals, amountOfGrades)

    surveyResult.proposals.forEachIndexed { i, prop ->
        val voteResult = surveyResult.judgments.filter { it.proposal == prop }
        voteResult.forEach { vote ->
            tally.collect(i, vote.grade.ordinal)
        }
    }

    val result: ResultInterface = deliberation.deliberate(tally)


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                modifier = modifier.padding(32.dp),
                text = "❝ ${surveyResult.asking} ❞",
                fontSize = 6.em,
            )
        }

        result.proposalResultsRanked.forEach { proposalResult ->
            Row {
                val rank = proposalResult.rank
                val proposalName = surveyResult.proposals[proposalResult.index]
                Text("#$rank  $proposalName")
            }


            Row {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    val proposalTally = tally.proposalsTallies[proposalResult.index]
                    var offsetX = 0F

                    grading.getGradesNames().forEachIndexed() { gradeIndex, gradeName ->

                        val w = (size.width
                                *
                                proposalTally.tally[gradeIndex].toFloat()
                                /
                                proposalTally.amountOfJudgments.toFloat()
                                )

                        drawRect(
                            color = grading.getGradeColor(gradeIndex),
                            size = Size(
                                w,
                                size.height
                            ),
                            topLeft = Offset(offsetX, 0F)
                        )

                        offsetX += w
                    }

                }

            }

            Row(modifier = Modifier.padding(8.dp)) {
                // cheap gap between proposals
            }

        }


        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onFinish() },
        ) { Text("Finish") }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewResultScreen(modifier: Modifier = Modifier) {
    val surveyResult = SurveyResult(
        asking = "Prézidan ?",
        proposals = listOf("Tonio", "Bobby", "Mario"),
        judgments = listOf(
            Judgment("Tonio", Grades.ARejeter),
            Judgment("Bobby", Grades.TresBien),
            Judgment("Mario", Grades.Excellent),
            Judgment("Tonio", Grades.Bien),
            Judgment("Bobby", Grades.Insuffisant),
            Judgment("Mario", Grades.Excellent),
            Judgment("Tonio", Grades.TresBien),
            Judgment("Bobby", Grades.TresBien),
            Judgment("Mario", Grades.Excellent),
        )
    )
    JmTheme {
        ResultScreen(
            surveyResult = surveyResult
        )
    }
}