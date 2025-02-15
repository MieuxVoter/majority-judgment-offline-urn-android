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
import androidx.compose.ui.input.key.Key.Companion.Ro
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.PropsResult
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.model.VoteResult
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    surveyResult: SurveyResult,
    onFinish : () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()
        .background(Color.White)
        .padding(8.dp)
    ){
        Text(
            modifier = Modifier,
            text = "VotingScreen"
        )
        Text("Q : ${surveyResult.asking}")

        surveyResult.props.forEach { prop ->
            Row {
                val voteResult = surveyResult.vote.filter { it.props == prop }
                Text("$prop Vote : ")
                voteResult.forEach { vote ->
                    Text(" ${vote.propsResult.name} ")
                }
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
        asking = "Prezidan ?",
        props = listOf("Toto", "Boby", "Mario"),
        vote = listOf(
            VoteResult("Toto", PropsResult.ARejeter),
            VoteResult("Boby", PropsResult.TresBien),
            VoteResult("Mario", PropsResult.Excellent),
            VoteResult("Toto", PropsResult.Bien),
            VoteResult("Boby", PropsResult.Insufficante),
            VoteResult("Mario", PropsResult.Excellent),
            VoteResult("Toto", PropsResult.TresBien),
            VoteResult("Boby", PropsResult.TresBien),
            VoteResult("Mario", PropsResult.Excellent),
        )
    )
    JmTheme {
        ResultScreen(
            surveyResult = surveyResult
        )
    }
}