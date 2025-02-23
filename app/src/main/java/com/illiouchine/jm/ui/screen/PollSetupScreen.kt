package com.illiouchine.jm.ui.screen

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.Screens
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.MUBottomBar
import com.illiouchine.jm.ui.composable.MUSnackbar
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun PollSetupScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    pollSetup: PollConfig = PollConfig(),
    onAddSubject: (String) -> Unit = {},
    onAddProposal: (String) -> Unit = {},
    onRemoveProposal: (String) -> Unit = {},
    setupFinished: () -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {

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
        bottomBar = {
            MUBottomBar(
                modifier = Modifier,
                selected = navController.currentDestination?.route ?: Screens.Home.name,
                onItemSelected = { destination -> navController.navigate(destination.id) }
            )
        },
    ) { innerPadding ->

        val context = LocalContext.current
        var proposal: String by remember { mutableStateOf("") }
        var subject: String by remember { mutableStateOf(pollSetup.subject) }

        fun generateProposalName(): String {
            return "${context.getString(R.string.proposal)} ${(65 + pollSetup.proposals.size).toChar()}"
        }

        // TODO: Perhaps use the 'fun' def syntax instead here ?
        // And move the logic in the ViewModel ?
        val addProposal: () -> Unit = {
            // Rule: if the proposal name is not specified, use a default
            if (proposal == "") {
                proposal = generateProposalName()
            }
            // Rule: proposals must have unique names
            if (pollSetup.proposals.contains(proposal)) {
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
                value = subject,
                onValueChange = {
                    subject = it
                    onAddSubject(subject)
                },
            )

            Text(stringResource(R.string.label_poll_proposals))
            Row {
                TextField(
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    value = proposal,
                    onValueChange = { proposal = it },
                    placeholder = { Text(generateProposalName()) },
                    keyboardActions = KeyboardActions(onDone = { addProposal() }),
                )
                Spacer(Modifier.size(16.dp))
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    onClick = { addProposal() },
                ) {
                    Text(stringResource(R.string.button_add))
                }
            }

            pollSetup.proposals.forEachIndexed { propIndex, propName ->

                if (propIndex > 0) {
                    HorizontalDivider(
                        // FIXME: Dark/Light/Theme support, using lightgray is a crutch
                        color = Color.LightGray,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 6.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(8.dp),
                        text = propName,
                    )
                    Spacer(Modifier.size(16.dp))
                    OutlinedButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        onClick = { onRemoveProposal(propName) },
                    ) { Text("x") }
                }
            }

            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.62f)
                    .padding(16.dp),
                enabled = pollSetup.proposals.size > 1,
                onClick = { setupFinished() },
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
            pollSetup = PollConfig(),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSetupSurveyScreenWithHugeNames(modifier: Modifier = Modifier) {
    JmTheme {
        PollSetupScreen(
            modifier = Modifier,
            pollSetup = PollConfig(
                subject = "Repas de ce soir, le Banquet Républicain de l'avènement du Jugement Majoritaire",
                proposals = listOf(
                    "Des nouilles aux champignons forestiers sur leur lit de purée de carottes urticantes",
                    "Du riz",
                    "Du riche",
                ),
            ),
        )
    }
}