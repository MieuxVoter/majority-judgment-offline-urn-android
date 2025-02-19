package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.graphics.ColorUtils
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun VotingScreen(
    modifier: Modifier = Modifier,
    survey: Survey,
    onFinish: (SurveyResult) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
//        Text("VotingScreen")
//        Text("Let's vote")

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                modifier = modifier.padding(32.dp),
                text = "❝ ${survey.subject} ❞",
                fontSize = 6.em,
            )
        }

//        Spacer(modifier = Modifier.size(8.dp))
//        Text(" ${survey.subject}")
//        Spacer(modifier = Modifier.size(8.dp))

        var currentPropsIndex: Int by remember { mutableIntStateOf(0) }
        var judgments: List<Judgment> by remember { mutableStateOf(emptyList()) }

        val voteNumber = judgments.size / survey.proposals.size
        Text("$voteNumber bulletins dans l'urne")

        if (currentPropsIndex >= survey.proposals.size) {
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
                        asking = survey.subject,
                        proposals = survey.proposals,
                        judgments = judgments,
                    )
                    onFinish(surveyResult)
                }
            ) { Text("Finish Voting") }
        } else {
            PropsSelection(
                survey = survey,
                currentProposalIndex = currentPropsIndex,
                onResultSelected = { result ->
                    val judgment = Judgment(
                        proposal = survey.proposals.get(currentPropsIndex),
                        grade = result,
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
    currentProposalIndex: Int,
    onResultSelected: (Int) -> Unit = {}
) {
    Text("Proposal : ${survey.proposals.get(currentProposalIndex)}")
    val context = LocalContext.current

    for (gradeIndex in 0..<survey.grading.getAmountOfGrades()) {
        val bgColor = survey.grading.getGradeColor(gradeIndex)
        // FIXME: I tried this, but it sucks, so I will use predetermined colors instead
        val contrastWithBlack = ColorUtils.calculateContrast(Color.Black.toArgb(), bgColor.toArgb())
        val contrastWithWhite = ColorUtils.calculateContrast(Color.White.toArgb(), bgColor.toArgb())
        val fgColor = if (contrastWithWhite > contrastWithBlack) Color.White else Color.Black

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onResultSelected(gradeIndex)
            },
            colors = ButtonColors(
                containerColor = bgColor,
                contentColor = fgColor,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White,
            ),
        ) {
            Text(context.getString(survey.grading.getGradeName(gradeIndex)).uppercase())
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewVotingScreen(modifier: Modifier = Modifier) {
    JmTheme {
        VotingScreen(
            survey = Survey(
                subject = "Best Prezidan ?",
                proposals = listOf("toto", "Mario", "JanBob"),
                grading = Quality7Grading(),
            )
        )
    }
}