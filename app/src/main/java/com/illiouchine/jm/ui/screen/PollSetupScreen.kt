package com.illiouchine.jm.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.composable.GradingSelectionRow
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.MjuSnackbarWithStringResId
import com.illiouchine.jm.ui.composable.ProposalRow
import com.illiouchine.jm.ui.composable.ProposalSelectionRow
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.composable.SubjectSelectionRow
import com.illiouchine.jm.ui.composable.ThemedHorizontalDivider
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.utils.displayed
import java.text.DateFormat
import java.util.Calendar


@Composable
fun PollSetupScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    pollSetupState: PollSetupViewModel.PollSetupViewState = PollSetupViewModel.PollSetupViewState(),
    onAddSubject: (Context, String) -> Unit = { _, _ -> },
    onAddProposal: (Context, String) -> Unit = { _, _ -> },
    onRemoveProposal: (String) -> Unit = {},
    onGradingSelected: (Grading) -> Unit = {},
    onSetupFinished: (PollConfig) -> Unit = {},
    onDismissFeedback: () -> Unit = {},
    onGetSubjectSuggestion: (String) -> Unit = {},
    onGetProposalSuggestion: (String) -> Unit = {},
    onClearSubjectSuggestion: () -> Unit = {},
    onClearProposalSuggestion: () -> Unit = {},
) {

    var finishButtonVisibility by remember { mutableStateOf(true) }
    var subject: String by remember { mutableStateOf(pollSetupState.pollSetup.subject) }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_setup"),
        snackbarHost = {
            MjuSnackbarWithStringResId(
                modifier = Modifier,
                textId = pollSetupState.feedback,
                onDismiss = {
                    onDismissFeedback()
                },
            )
        },
        bottomBar = {
            MjuBottomBar(
                modifier = Modifier,
                selected = Screens.Home,
                onItemSelected = { destination -> navController.navigate(destination) }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = pollSetupState.pollSetup.proposals.size > 1 && !finishButtonVisibility,
            ) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        // Rule: if the poll's subject was not provided, use a default.
                        if (subject.isBlank()) {
                            onAddSubject(context, subject)
                        }
                        onSetupFinished(pollSetupState.pollSetup)
                    },
                    icon = { },
                    text = { Text(stringResource(R.string.button_let_s_go)) },
                )
            }
        },
    ) { innerPadding ->

        var proposal: String by remember { mutableStateOf("") }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(state = ScrollState(initial = 0)),
        ) {
            ScreenTitle(text = stringResource(R.string.title_poll_setup))

            SubjectSelectionRow(
                modifier = Modifier,
                subject = subject,
                subjectSuggestion = pollSetupState.subjectSuggestion,
                onSuggestionSelected = {
                    subject = it
                    onAddSubject(context, subject)
                    onGetSubjectSuggestion("")
                },
                onSubjectChange = {
                    subject = it
                    onAddSubject(context, subject)
                    if (it.length > 2) {
                        onGetSubjectSuggestion(it)
                    } else {
                        onClearSubjectSuggestion()
                    }
                },
                onClearSuggestion = {
                    onClearSubjectSuggestion()
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProposalSelectionRow(
                modifier = Modifier,
                proposal = proposal,
                onProposalChange = {
                    proposal = it
                    if (it.length > 2) {
                        onGetProposalSuggestion(it)
                    } else {
                        onClearProposalSuggestion()
                    }
                },
                onAddProposal = {
                    onAddProposal(context, it)
                    proposal = ""
                },
                proposalSuggestion = pollSetupState.proposalSuggestion,
                onProposalSelected = {
                    proposal = it
                    onClearProposalSuggestion()
                },
                onClearSuggestion = {
                    onClearProposalSuggestion()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            pollSetupState.pollSetup.proposals.reversed().forEachIndexed { propIndex, propName ->

                if (propIndex > 0) {
                    ThemedHorizontalDivider()
                }

                ProposalRow(
                    modifier = Modifier,
                    proposal = propName,
                    onRemoveClicked = { onRemoveProposal(it) }
                )
            }

            GradingSelectionRow(
                modifier = Modifier,
                grading = pollSetupState.pollSetup.grading,
                onGradingSelected = {
                    onGradingSelected(it)
                },
            )

            // Rule: A poll should have more than 1 proposal.
            Button(
                modifier = Modifier
                    .testTag("setup_submit")
                    .displayed { finishButtonVisibility = it }
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.62f)
                    .padding(16.dp),
                enabled = pollSetupState.pollSetup.proposals.size > 1,
                onClick = {
                    val newPoll = pollSetupState.pollSetup.addSubjectIfEmpty(context)
                    onSetupFinished(newPoll)
                },
            ) {
                Text(stringResource(R.string.button_let_s_go))
            }
        }
    }
}

//FIXME : This rule should be moved in VM.
// need typeSafe navigation to work correctly
fun generateSubject(context: Context): String {
    return buildString {
        append(context.getString(R.string.poll_of))
        append(" ")
        append(DateFormat.getDateInstance().format(Calendar.getInstance().time))
    }
}

//FIXME : This rule should be moved in VM.
// need typeSafe navigation to work correctly
private fun PollConfig.addSubjectIfEmpty(context: Context): PollConfig {
    val newPoll = copy(
        subject = subject.ifEmpty { generateSubject(context) }
    )
    return newPoll
}


@Preview(showSystemUi = true)
@Composable
fun PreviewSetupSurveyScreen(modifier: Modifier = Modifier) {
    JmTheme {
        PollSetupScreen(
            modifier = Modifier,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSetupSurveyScreenWithHugeNames(modifier: Modifier = Modifier) {
    JmTheme {
        PollSetupScreen(
            modifier = Modifier,
            pollSetupState = PollSetupViewModel.PollSetupViewState(
                pollSetup = PollConfig(
                    subject = "Repas de ce soir, le Banquet Républicain de l'avènement du Jugement Majoritaire",
                    proposals = listOf(
                        "Des nouilles aux champignons forestiers sur leur lit de purée de carottes urticantes",
                        "Du riz",
                        "Du riche",
                    ),
                ),
            ),
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewSetupSurveyScreenWithLotsOfPropals(modifier: Modifier = Modifier) {
    JmTheme {
        PollSetupScreen(
            modifier = Modifier,
            pollSetupState = PollSetupViewModel.PollSetupViewState(
                pollSetup = PollConfig(
                    subject = "Repas de ce soir, le Banquet Républicain de l'avènement du Jugement Majoritaire",
                    proposals = listOf(
                        "Des nouilles aux champignons forestiers sur leur lit de purée de carottes urticantes",
                        "Du riz",
                        "Du riche",
                        "Des Spaghetti Carbonara",
                        "Poulet Tikka Masala",
                        "Sushi",
                        "Tacos au Bœuf"
                    ),
                ),
            ),
        )
    }
}