package com.illiouchine.jm.ui.screen

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun VoteSummaryScreen(
    modifier: Modifier = Modifier,
    surveyResult: SurveyResult,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = ScrollState(initial = 0))
            .padding(8.dp),
    ) {
        PollSubject(
            poll = surveyResult.survey,
        )

        Text(
            text = "You're almost done !"
        )
        Text(
            text = "Here's a summary of your judgments :",
        )

        Spacer(Modifier.height(32.dp))

        surveyResult.survey.proposals.forEachIndexed { proposalIndex, proposal ->
            Row {
                Text(proposal)
                Text(" is ")
                Text(stringResource(surveyResult.survey.grading.getGradeName(0)))
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Is that accurate ?",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {

            OutlinedButton(
                onClick = onCancel,
            ) {
                Text(
                    text = "No, do over",
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onConfirm,
            ) {
                Text(
                    text = "Yes, proceed",
                )
            }

        }

    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewVoteSummaryScreen(modifier: Modifier = Modifier) {
    // FIXME: figure out how to reuse this across previews
    // I looked into data providers, but WOW it's complicated >.<
    val surveyResult = SurveyResult(
        survey = Survey(
            subject = "Prézidaaanh ?",
            proposals = listOf("Tonio", "Bobby", "Mario"),
            grading = Quality7Grading(),
        ),
        judgments = listOf(
            Judgment("Tonio", 0),
            Judgment("Bobby", 5),
            Judgment("Mario", 6),
            Judgment("Tonio", 4),
            Judgment("Bobby", 1),
            Judgment("Mario", 6),
            Judgment("Tonio", 5),
            Judgment("Bobby", 5),
            Judgment("Mario", 6),
        ),
    )
    JmTheme {
        VoteSummaryScreen(
            surveyResult = surveyResult,
        )
    }
}