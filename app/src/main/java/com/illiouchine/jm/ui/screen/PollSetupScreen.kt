package com.illiouchine.jm.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.illiouchine.jm.Screens
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.GradingSelectionRow
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.theme.DeleteColor
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.SeparatorDark
import com.illiouchine.jm.ui.theme.SeparatorLight
import com.illiouchine.jm.ui.utils.displayed
import java.text.DateFormat
import java.util.Calendar


@Composable
fun PollSetupScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    pollSetupState: PollSetupViewModel.PollSetupViewState = PollSetupViewModel.PollSetupViewState(),
    onAddSubject: (String) -> Unit = {},
    onAddProposal: (String) -> Unit = {},
    onRemoveProposal: (String) -> Unit = {},
    onGradingSelected: (Grading) -> Unit = {},
    onSetupFinished: () -> Unit = {},
    onDismissFeedback: () -> Unit = {},
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
            MjuSnackbar(
                modifier = Modifier,
                text = pollSetupState.feedback,
                onDismiss = {
                    onDismissFeedback()
                },
            )
        },
        bottomBar = {
            MjuBottomBar(
                modifier = Modifier,
                selected = navController.currentDestination?.route ?: Screens.Home.name,
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

        fun generateProposalName(): String {
            return buildString {
                append(context.getString(R.string.proposal))
                append(" ")
                append((65 + pollSetupState.pollSetup.proposals.size).toChar())
            }
        }

        val addProposal: () -> Unit = {
            // Rule: if the proposal name is not specified, use a default
            if (proposal == "") {
                proposal = generateProposalName()
            }
            // Rule: proposals must have unique names
            if (pollSetupState.pollSetup.proposals.contains(proposal)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_proposal_name_already_exists),
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                onAddProposal(proposal)
                proposal = ""
            }
        }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(state = ScrollState(initial = 0)),
        ) {

            Text(stringResource(R.string.label_poll_subject))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                placeholder = { Text(generateSubject()) },
                value = subject,
                onValueChange = {
                    subject = it
                    onAddSubject(subject)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.label_poll_proposals))
            Row {
                TextField(
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    value = proposal,
                    onValueChange = { proposal = it },
                    placeholder = { Text(generateProposalName()) },
                    keyboardActions = KeyboardActions(onDone = if (proposal.isBlank()) {
                        // A null value indicates that the default implementation should be executed
                        // This helps older Android version users to close the keyboard
                        null
                    } else {
                        {
                            addProposal()
                        }
                    }),
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.testTag("setup_add_proposal"),
                            onClick = { addProposal() },
                        ) {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Outlined.Add,
                                contentDescription = stringResource(R.string.button_add),
                            )
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            pollSetupState.pollSetup.proposals.reversed().forEachIndexed { propIndex, propName ->

                if (propIndex > 0) {
                    HorizontalDivider(
                        color = if (isSystemInDarkTheme()) SeparatorDark else SeparatorLight,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        text = propName,
                    )
                    Spacer(
                        Modifier
                            .height(16.dp)
                            .padding(8.dp)
                    )
                    IconButton(
                        onClick = { onRemoveProposal(propName) },
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                            tint = DeleteColor,
                        )
                    }
                }
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
                    // Rule: if the poll's subject was not provided, use a default.
                    if (subject.isBlank()) {
                        subject = generateSubject()
                        onAddSubject(subject)
                    }
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