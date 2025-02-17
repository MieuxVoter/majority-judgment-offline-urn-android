package com.illiouchine.jm.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Grades
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun VotingScreen(
    modifier: Modifier = Modifier,
    survey: Survey,
    onFinish : (SurveyResult) -> Unit = {}
) {
    Column(modifier = modifier
        .fillMaxSize()
//        .background(Color.White)
        .padding(8.dp)
    ) {
        Text("VotingScreen")
        Text("Let's vote")
        Spacer(modifier = Modifier.size(8.dp))
        Text("Q : ${survey.asking}")
        Spacer(modifier = Modifier.size(8.dp))

        var currentPropsIndex: Int by remember { mutableIntStateOf(0) }
        var judgments: List<Judgment> by remember { mutableStateOf(emptyList()) }

        val voteNumber = judgments.size / survey.props.size
        Text("$voteNumber bulletins dans l'urne")

        if (currentPropsIndex >= survey.props.size){
            Text("A Voté !")
            Text("Votre participation a bien été prise en compte. Vous pouvez maintenant passer cet appareil au prochain participant")
            Button(
                onClick = {
                    currentPropsIndex = 0
                }
            ) { Text("Next votant") }
            Button(
                onClick = {
                    val surveyResult = SurveyResult(
                        asking = survey.asking,
                        proposals = survey.props,
                        judgments = judgments
                    )
                    onFinish(surveyResult)
                }
            ) { Text("Finish Voting") }
        } else {
            PropsSelection(
                survey = survey,
                currentPropsIndex = currentPropsIndex,
                onResultSelected = { result ->
                    val judgment = Judgment(
                        proposal = survey.props.get(currentPropsIndex),
                        grade = result
                    )
                    judgments = judgments + judgment
                    currentPropsIndex++
                }
            )
        }
    }
}

@Composable
private fun PropsSelection(
    survey: Survey,
    currentPropsIndex: Int,
    onResultSelected: (Grades) -> Unit = {}
) {
    Text("Props : ${survey.props.get(currentPropsIndex)}")

    for (value in Grades.entries) {
        Button(onClick = {
            onResultSelected(value)
        }) {
            Text(value.toString())
        }
    }
}

@Preview
@Composable
fun PreviewVotingScreen(modifier: Modifier = Modifier) {
    JmTheme {
        VotingScreen(
            survey = Survey("Best Prezidan ?", listOf("toto", "Mario", "JanBob"))
        )
    }
}