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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import com.illiouchine.jm.ui.composable.GradingSelectionRow
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.MjuSnackbarWithStringResId
import com.illiouchine.jm.ui.composable.ProposalRow
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
    onAddSubject: (String) -> Unit = {},
    onAddProposal: (Context, String) -> Unit = { _, _ -> },
    onRemoveProposal: (String) -> Unit = {},
    onGradingSelected: (Grading) -> Unit = {},
    onSetupFinished: () -> Unit = {},
    onDismissFeedback: () -> Unit = {},
    onGetSubjectSuggestion: (String) -> Unit = {},
    onGetProposalSuggestion: (String) -> Unit = {},
) {

    var finishButtonVisibility by remember { mutableStateOf(true) }

    var subject: String by remember { mutableStateOf(pollSetupState.pollSetup.subject) }
    val context = LocalContext.current

    fun generateSubject(): String {
        return buildString {
            append(context.getString(R.string.poll_of))
            append(" ")
            append(DateFormat.getDateInstance().format(Calendar.getInstance().time))
        }
    }

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
                selected = navController.currentDestination?.route ?: Navigator.Screens.Home.name,
                onItemSelected = { destination -> navController.navigate(destination.id) }
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
                            subject = generateSubject()
                            onAddSubject(subject)
                        }
                        onSetupFinished()
                    },
                    icon = { },
                    text = { Text(stringResource(R.string.button_let_s_go)) },
                )
            }
        },
    ) { innerPadding ->

        var proposal: String by remember { mutableStateOf("") }
        var subject: String by remember { mutableStateOf(pollSetupState.pollSetup.subject) }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(state = ScrollState(initial = 0)),
        ) {
            SubjectSelectionRow(
                modifier = Modifier,
                subject = subject,
                subjectSuggestion = pollSetupState.subjectSuggestion,
                onSuggestionSelected = {
                    subject = it
                    onAddSubject(subject)
                    onGetSubjectSuggestion("")
                },
                onSubjectChange = {
                    subject = it
                    onAddSubject(subject)
                    if (it.length > 2) {
                        onGetSubjectSuggestion(it)
                    } else {
                        onGetSubjectSuggestion("")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.label_poll_proposals))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                value = proposal,
                onValueChange = {
                    proposal = it
                    if (it.length > 2) {
                        onGetProposalSuggestion(it)
                    } else {
                        onGetProposalSuggestion("")
                    }
                },
                placeholder = { Text("Entrez vos propositions...") },
                keyboardActions = KeyboardActions(onDone = if (proposal.isBlank()) {
                    // A null value indicates that the default implementation should be executed
                    // This helps older Android version users to close the keyboard
                    null
                } else {
                    {
                        onAddProposal(context, proposal)
                        proposal = ""
                    }
                }),
                trailingIcon = {
                    IconButton(
                        modifier = Modifier.testTag("setup_add_proposal"),
                        onClick = {
                            onAddProposal(context, proposal)
                            proposal = ""
                        },
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.button_add),
                        )
                    }
                },
            )
            AnimatedVisibility(
                visible = pollSetupState.proposalSuggestion.isNotEmpty()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    pollSetupState.proposalSuggestion.take(3)
                        .forEachIndexed { index, proposalSuggestion ->
                            if (index > 0) {
                                ThemedHorizontalDivider()
                            }
                            TextButton(
                                modifier = Modifier.padding(8.dp),
                                onClick = {
                                    proposal = proposalSuggestion
                                    onGetProposalSuggestion("")
                                }
                            ) {
                                Text(proposalSuggestion)
                            }
                        }
                }
            }

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
                    onSetupFinished()
                },
            ) {
                Text(stringResource(R.string.button_let_s_go))
            }
        }
    }
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