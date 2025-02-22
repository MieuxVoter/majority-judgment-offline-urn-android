package com.illiouchine.jm.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollResult
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.ui.composable.MUSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VotingScreen(
    modifier: Modifier = Modifier,
    poll: Poll,
    onFinish: (PollResult) -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {

    var currentProposalIndex: Int by remember { mutableIntStateOf(0) }
    var judgments: List<Judgment> by remember { mutableStateOf(emptyList()) }
    var confirmed: Boolean by remember { mutableStateOf(false) }

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

            PollSubject(
                poll = poll,
            )

            if (currentProposalIndex >= poll.proposals.size) {

                val pollResult = PollResult(
                    poll = poll,
                    judgments = judgments,
                )

                if (!confirmed) {

                    VoteSummaryScreen(
                        pollResult = pollResult,
                        onConfirm = {
                            confirmed = true
                        },
                        onCancel = {
                            judgments = judgments.subList(0, judgments.size - poll.proposals.size)
                            currentProposalIndex = 0
                        },
                    )

                } else {

                    Text("A Voté !")
                    Text("Votre participation a bien été prise en compte. Vous pouvez maintenant passer cet appareil au prochain participant")
                    Button(
                        onClick = {
                            currentProposalIndex = 0
                            confirmed = false
                        }
                    ) { Text(stringResource(R.string.button_next_participant)) }
                    Button(
                        onClick = {
                            onFinish(pollResult)
                        }
                    ) { Text(stringResource(R.string.button_end_the_poll)) }

                }
            } else {
                PropsSelection(
                    poll = poll,
                    currentProposalIndex = currentProposalIndex,
                    // TODO: hoist this in the view model
                    onResultSelected = { result ->
                        val judgment = Judgment(
                            proposal = poll.proposals.get(currentProposalIndex),
                            grade = result,
                        )
                        judgments = judgments + judgment
                        currentProposalIndex++
                    }
                )
            }


            val amountOfBallots = judgments.size / poll.proposals.size
            Spacer(
                modifier = Modifier.padding(12.dp),
            )
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                // FIXME: this syntax feels weird…  Is there a better way ?
                val ballotsString = if (amountOfBallots <= 1)
                    stringResource(R.string.ballot)
                else
                    stringResource(R.string.ballots)

                Text(
                    "${amountOfBallots} ${ballotsString} " + stringResource(R.string.in_the_urn)
                )
            }
        }
    }



    // Rule: going BACK cancels the last cast judgment, if any.
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
    poll: Poll,
    currentProposalIndex: Int,
    onResultSelected: (Int) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.all_things_considered_i_think),
            fontStyle = FontStyle.Italic,
        )
    }
    // Using Row here instead won't center the text on the phone, even though it does in the preview
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = poll.proposals[currentProposalIndex],
            textAlign = TextAlign.Center,
            fontSize = 8.em,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.verb_is),
            fontStyle = FontStyle.Italic,
        )
    }

    val context = LocalContext.current
    var selectedGradeIndex: Int? by remember { mutableStateOf(null) }

    for (gradeIndex in 0..<poll.grading.getAmountOfGrades()) {
        val bgColor = poll.grading.getGradeColor(gradeIndex)
        val fgColor = poll.grading.getGradeTextColor(gradeIndex)

        val interactionSource = remember { MutableInteractionSource() }
        val interactionSourceIsPressed by interactionSource.collectIsFocusedAsState()
        val coroutine = rememberCoroutineScope()

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (interactionSourceIsPressed) 80.dp else 64.dp)
//                .clickable(
//                    interactionSource = interactionSource,
//                    indication = createRippleModifierNode(
//                        interactionSource = interactionSource,
//                        bounded = false,
//                        radius = 64.dp,
//                        color = { Color.Magenta },
//                        rippleAlpha = { RippleAlpha(
//                            // FIXME
//                            draggedAlpha = 0.1f,
//                            focusedAlpha = 0.9f,
//                            hoveredAlpha = 1.0f,
//                            pressedAlpha = 0.62f,
//                        ) },
//                    ),
//                ) {}
                .padding(top = 12.dp),

            enabled = (selectedGradeIndex == null) || (selectedGradeIndex == gradeIndex),

            onClick = {
                if (selectedGradeIndex != null) {
                    return@Button
                }
                selectedGradeIndex = gradeIndex
                coroutine.launch {
                    delay(150)
                    onResultSelected(gradeIndex)
                    selectedGradeIndex = null
                }
            },

            colors = ButtonColors(
                containerColor = bgColor,
                contentColor = fgColor,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White,
            ),

//            interactionSource = interactionSource,
        ) {
            Text(
                text = context.getString(poll.grading.getGradeName(gradeIndex)).uppercase(),
                fontSize = 5.em,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewVotingScreen(modifier: Modifier = Modifier) {
    JmTheme {
        VotingScreen(
            poll = Poll(
                subject = "Best Prezidan ?",
                proposals = listOf("That candidate with a long name-san", "Mario", "JanBob"),
                grading = Quality7Grading(),
            )
        )
    }
}