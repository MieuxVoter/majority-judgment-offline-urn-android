package com.illiouchine.jm.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.model.Grades
import com.illiouchine.jm.model.Judgment
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

    val result: ResultInterface = deliberation.deliberate(tally)


    Column(modifier = modifier
        .fillMaxSize()
        .padding(8.dp)
    ){
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
        }

        Row {
            // TODO : Use Canvas to add custom drawing
            Canvas(modifier = Modifier.size(200.dp)) {
                val canvasQuadrantSize = size.minDimension / 2F
                drawRect(
                    color = Color.Magenta,
                    size = Size(canvasQuadrantSize, canvasQuadrantSize)
                )
                drawCircle(
                    color = Color.Cyan,
                    radius = size.minDimension /2,
                    center = this.center,
                    alpha = 0.2f,
                    style = Fill,
                    colorFilter = null,
                    blendMode = BlendMode.Color,
                )
            }

        }


        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {onFinish()},
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