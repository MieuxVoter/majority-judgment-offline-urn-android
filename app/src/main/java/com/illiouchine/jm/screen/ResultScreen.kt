package com.illiouchine.jm.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Grades
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.ui.theme.JmTheme
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    surveyResult: SurveyResult,
    onFinish : () -> Unit = {}
) {

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

    val result = deliberation.deliberate(tally)


    Column(modifier = modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(8.dp)
    ){
        Text(
            modifier = Modifier,
            text = "Délibération"
        )
        Text("Q : ${surveyResult.asking}")

        surveyResult.proposals.forEachIndexed { i, prop ->
            Row {
                val rank = result.proposalResults[i].rank
                Text("#$rank  $prop")

                // We can't show this as-is, it's not anonymous
//                val proposalJudgments = surveyResult.judgments.filter { it.proposal == prop }
//                Text(" (")
//                proposalJudgments.forEach { judgment ->
//                    Text(" ${judgment.grade.name} ")
//                }
//                Text(")")
            }
        }
        Button(
            onClick = {onFinish()}
        ) { Text("Finish") }
    }
}


@Preview
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