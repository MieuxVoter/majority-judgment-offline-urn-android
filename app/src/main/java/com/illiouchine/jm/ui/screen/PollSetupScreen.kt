package com.illiouchine.jm.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.composable.GradingSelectionRow
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.MjuSnackbarWithStringResId
import com.illiouchine.jm.ui.composable.ProposalRow
import com.illiouchine.jm.ui.composable.ProposalSelectionRow
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.composable.SubjectSelectionRow
import com.illiouchine.jm.ui.composable.ThemedHorizontalDivider
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.previewdatabuilder.PreviewDataBuilder
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import com.illiouchine.jm.ui.utils.displayed
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun PollSetupScreen(
    modifier: Modifier = Modifier,
    onBottomBarItemSelected: (item: NavKey) -> Unit = {},
    pollSetupState: PollSetupViewModel.PollSetupViewState = PollSetupViewModel.PollSetupViewState(),
    onAddSubject: (Context, String) -> Unit = { _, _ -> },
    onAddProposal: (Context, String) -> Unit = { _, _ -> },
    onRemoveProposal: (String) -> Unit = {},
    onGradingSelected: (Grading) -> Unit = {},
    onSetupFinished: (Context) -> Unit = {},
    onDismissFeedback: () -> Unit = {},
    onGetSubjectSuggestion: (String) -> Unit = {},
    onGetProposalSuggestion: (String) -> Unit = {},
    onClearSubjectSuggestion: () -> Unit = {},
    onClearProposalSuggestion: () -> Unit = {},
) {
    val context = LocalContext.current
    var finishButtonVisibility by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("setup_screen"),
        snackbarHost = {
            MjuSnackbarWithStringResId(
                modifier = Modifier,
                textId = pollSetupState.feedback,
                onDismiss = { onDismissFeedback() },
            )
        },
        bottomBar = {
            MjuBottomBar(
                modifier = Modifier,
                selected = Screens.Home,
                onItemSelected = { destination ->
                    coroutineScope.launch {
                        onBottomBarItemSelected(destination)
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = pollSetupState.config.proposals.size > 1 && !finishButtonVisibility,
            ) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(Theme.spacing.medium),
                    onClick = { onSetupFinished(context) },
                    icon = { },
                    text = { Text(stringResource(R.string.button_let_s_go)) },
                )
            }
        },
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        var proposal: String by rememberSaveable { mutableStateOf("") }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(Theme.spacing.medium)
                .verticalScroll(scrollState),
        ) {
            ScreenTitle(text = stringResource(R.string.title_poll_setup))

            SubjectSelectionRow(
                modifier = Modifier,
                subject = pollSetupState.config.subject,
                subjectSuggestion = pollSetupState.subjectSuggestion.toImmutableList(),
                onSuggestionSelected = {
                    onAddSubject(context, it)
                    onGetSubjectSuggestion("")
                },
                onSubjectChange = {
                    onAddSubject(context, it)
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
            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            ProposalSelectionRow(
                modifier = Modifier,
                proposal = proposal,
                onProposalChange = {
                    proposal = it
                    if (it.length > 1) {
                        onGetProposalSuggestion(it)
                    } else {
                        onClearProposalSuggestion()
                    }
                },
                onAddProposal = {
                    onAddProposal(context, it)
                    proposal = ""
                },
                proposalSuggestion = pollSetupState.proposalSuggestion.toImmutableList(),
                onProposalSelected = {
                    proposal = it
                    onClearProposalSuggestion()
                },
                onClearSuggestion = {
                    onClearProposalSuggestion()
                }
            )

            Spacer(modifier = Modifier.height(Theme.spacing.small))

            pollSetupState.config.proposals.reversed().forEachIndexed { propIndex, propName ->

                if (propIndex > 0) {
                    ThemedHorizontalDivider()
                }

                ProposalRow(
                    modifier = Modifier,
                    proposal = propName,
                    onRemoveClicked = { onRemoveProposal(it) },
                )
            }

            GradingSelectionRow(
                modifier = Modifier,
                grading = pollSetupState.config.grading,
                onGradingSelected = { onGradingSelected(it) },
            )

            // Rule: A poll should have more than 1 proposal.
            Button(
                modifier = Modifier
                    .testTag("setup_submit")
                    .displayed { finishButtonVisibility = it }
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.62f)
                    .padding(Theme.spacing.medium),
                enabled = pollSetupState.config.proposals.size > 1,
                onClick = { onSetupFinished(context) },
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
            modifier = modifier,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSetupSurveyScreenWithHugeNames(modifier: Modifier = Modifier) {
    JmTheme {
        PollSetupScreen(
            modifier = modifier,
            pollSetupState = PollSetupViewModel.PollSetupViewState(
                config = PreviewDataBuilder.pollConfig(index = 4)
            ),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSetupSurveyScreenWithLotsOfProposals(modifier: Modifier = Modifier) {
    JmTheme {
        PollSetupScreen(
            modifier = modifier,
            pollSetupState = PollSetupViewModel.PollSetupViewState(
                config = PreviewDataBuilder.pollConfig(index = 5)
            ),
        )
    }
}
