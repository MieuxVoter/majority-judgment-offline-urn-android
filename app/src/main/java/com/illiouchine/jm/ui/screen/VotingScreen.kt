package com.illiouchine.jm.ui.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun VotingScreen(
    modifier: Modifier = Modifier,
    survey: Survey,
    onFinish: (SurveyResult) -> Unit = {},
) {

    var currentProposalIndex: Int by remember { mutableIntStateOf(0) }
    var judgments: List<Judgment> by remember { mutableStateOf(emptyList()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = ScrollState(initial = 0))
            .padding(8.dp),
    ) {

        PollSubject(
            poll = survey,
        )

//        Spacer(modifier = Modifier.size(8.dp))
//        Text(" ${survey.subject}")
//        Spacer(modifier = Modifier.size(8.dp))

        if (currentProposalIndex >= survey.proposals.size) {
            Text("A Voté !")
            Text("Votre participation a bien été prise en compte. Vous pouvez maintenant passer cet appareil au prochain participant")
            Button(
                onClick = {
                    currentProposalIndex = 0
                }
            ) { Text(stringResource(R.string.button_next_participant)) }
            Button(
                onClick = {
                    val surveyResult = SurveyResult(
                        subject = survey.subject,
                        proposals = survey.proposals,
                        grading = survey.grading,
                        judgments = judgments,
                    )
                    onFinish(surveyResult)
                }
            ) { Text(stringResource(R.string.button_end_the_poll)) }
        } else {
            PropsSelection(
                survey = survey,
                currentProposalIndex = currentProposalIndex,
                onResultSelected = { result ->
                    val judgment = Judgment(
                        proposal = survey.proposals.get(currentProposalIndex),
                        grade = result,
                    )
                    judgments = judgments + judgment
                    currentProposalIndex++
                }
            )
        }


        val amountOfBallots = judgments.size / survey.proposals.size
        Spacer(
            modifier = Modifier.padding(12.dp),
        )
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            val ballotsString = if (amountOfBallots <= 1)
                stringResource(R.string.ballot)
            else
                stringResource(R.string.ballots)
            Text(
                "${amountOfBallots} " + ballotsString + " " + stringResource(R.string.in_the_urn)
            )
        }

    }

    BackHandler(
        enabled = (currentProposalIndex > 0),
    ) {
        if (currentProposalIndex > 0) {
            currentProposalIndex--
            judgments = judgments.subList(0, judgments.size - 1)
        }
    }
}

@Composable
private fun PropsSelection(
    survey: Survey,
    currentProposalIndex: Int,
    onResultSelected: (Int) -> Unit = {}
) {
    Row(
        // FIXME: WTF IS HAPPENING HERE
//        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
        Text(
            text = "All things considered, I think",
        )
    }
    Text(
        text = survey.proposals.get(currentProposalIndex),
        fontSize = 6.em,
    )
    Text(
        text = "is"
    )

    val context = LocalContext.current

    for (gradeIndex in 0..<survey.grading.getAmountOfGrades()) {
        val bgColor = survey.grading.getGradeColor(gradeIndex)
        val fgColor = survey.grading.getGradeTextColor(gradeIndex)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(top = 12.dp),
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