package com.illiouchine.jm.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.composable.BallotCountRow
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme
import java.util.Locale
import kotlin.math.round


@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    state: PollResultViewModel.PollResultViewState,
    onFinish: () -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    val poll = state.poll!!
    val result = state.result!!
    val tally = state.tally!!
    val grading = poll.pollConfig.grading

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
            PollSubject(
                subject = poll.pollConfig.subject,
            )

            BallotCountRow(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                ballots = poll.ballots,
            )

            Spacer(modifier = Modifier.padding(8.dp))

            result.proposalResultsRanked.forEach { proposalResult ->
                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
                    val rank = proposalResult.rank
                    val proposalName = poll.pollConfig.proposals[proposalResult.index]
                    val medianGrade = proposalResult.analysis.medianGrade
                    val medianGradeName =
                        stringResource(poll.pollConfig.grading.getGradeName(medianGrade))
                    Text(
                        modifier = Modifier.padding(end = 12.dp),
                        fontSize = 24.sp,
                        text = "#$rank",
                    )
                    Text(
                        text = "$proposalName   ($medianGradeName)",
                    )
                }

                Row {
                    // TODO: refactor this into LinearMeritProfileCanvas @Composable

                    val textMeasurer = rememberTextMeasurer()
                    val contrastedColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                    val waitAnimation = remember { Animatable(0f) } // :(|) oOok
                    val widthAnimation = remember { Animatable(0f) }
                    val outlineAnimation = remember { Animatable(0f) }
                    val percentageAnimation = remember { Animatable(0f) }

                    LaunchedEffect("apparition") {
                        // TODO: figure out how to NOT use the waitAnimation hack
                        waitAnimation.animateTo(1f)
                        waitAnimation.animateTo(0f)
                        waitAnimation.animateTo(1f)
                        waitAnimation.animateTo(0f)
                        widthAnimation.animateTo(1f, tween(1500))
                        outlineAnimation.animateTo(1f, tween(400))
                        waitAnimation.animateTo(1f)
                        waitAnimation.animateTo(0f)
                        percentageAnimation.animateTo(1f, tween(3000))
                    }

                    // Draw the linear merit profile of the proposal.
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    ) {
                        val proposalTally = tally.proposalsTallies[proposalResult.index]
                        var offsetX = 0f
                        val medianGradeOutline = Path()
                        val balancedGradeWidth = size.width / grading.getAmountOfGrades()

                        for (gradeIndex in (0..<grading.getAmountOfGrades()).reversed()) {
                            var gradeWidth = ( // appalling indentation, please help
                                    (size.width * proposalTally.tally[gradeIndex].toFloat())
                                            /
                                            proposalTally.amountOfJudgments.toFloat()
                                    )
                            gradeWidth = lerp(balancedGradeWidth, gradeWidth, widthAnimation.value)
                            val gradeRectSize = Size(gradeWidth, size.height)
                            val gradeRectOffset = Offset(offsetX, 0f)

                            // Fill a rectangle with the color of the grade
                            drawRect(
                                color = grading.getGradeColor(gradeIndex),
                                size = gradeRectSize,
                                topLeft = gradeRectOffset,
                            )

                            // Show the percentage under each grade with at least one judgment
                            if (gradeWidth > 0) {
                                val percentage = 100f * gradeWidth / size.width
                                val approximate = if (round(percentage) != percentage) {
                                    "~"
                                } else {
                                    ""
                                }
                                val measuredText =
                                    textMeasurer.measure(
                                        text = AnnotatedString(
                                            String.format(
                                                locale = Locale.FRANCE,
                                                format = "$approximate%.0f%%",
                                                percentage,
                                            )
                                        ),
                                        style = TextStyle(fontSize = 10.sp),
                                    )
                                drawText(
                                    textLayoutResult = measuredText,
                                    topLeft = gradeRectOffset + Offset(
                                        (gradeRectSize.width - measuredText.size.width) * 0.5f,
                                        size.height + 8,
                                    ),
                                    color = contrastedColor,
                                    alpha = 0.8f * percentageAnimation.value,
                                )
                            }

                            // Outline only the median grade
                            if (gradeIndex == proposalResult.analysis.medianGrade) {
                                medianGradeOutline.addRect(
                                    Rect(
                                        size = gradeRectSize,
                                        offset = gradeRectOffset,
                                    )
                                )
                            }

                            offsetX += gradeWidth
                        }

                        // Draw the median grade outline *after* drawing all the grade rectangles
                        drawPath(
                            color = contrastedColor,
                            path = medianGradeOutline,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                join = StrokeJoin.Bevel,
                            ),
                            alpha = outlineAnimation.value,
                        )

                        // Vertical line in the middle, marking the median grade.
                        drawLine(
                            color = contrastedColor,
                            start = Offset(size.width * 0.5f, -11f),
                            end = Offset(size.width * 0.5f, size.height + 12f),
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(10f, 5f),
                                phase = -0.5f,
                            ),
                            strokeWidth = 1.dp.toPx(),
                            alpha = outlineAnimation.value,
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(12.dp))
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
            subject = "Who for Prézidaaanh ?",
            proposals = listOf(
                "Luigi the green plumber with a mustache and a long name",
                "Bobby",
                "Mario",
            ),
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
//            Ballot(
//                judgments = listOf(
//                    Judgment(0, 3),
//                    Judgment(1, 0),
//                    Judgment(2, 1),
//                )
//            ),
        ),
    )
    val pollResultViewModel = PollResultViewModel(Navigator())
    pollResultViewModel.initializePollResult(poll)
    val state = pollResultViewModel.pollResultViewState.collectAsState().value
    JmTheme {
        ResultScreen(
            state = state,
        )
    }
}