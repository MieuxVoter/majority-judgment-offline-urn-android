package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.DefaultNavigator
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.PollDeletionConfirmationDialog
import com.illiouchine.jm.ui.composable.PollSummary
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewState: HomeViewModel.HomeViewState = HomeViewModel.HomeViewState(),
    navigator: Navigator = DefaultNavigator(),
    onSetupBlankPoll: () -> Unit = {},
    onSetupClonePoll: (poll: Poll) -> Unit = {},
    onResumePoll: (poll: Poll) -> Unit = {},
    onShowResult: (poll: Poll) -> Unit = {},
    onDeletePoll: (poll: Poll) -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        bottomBar = {
            MjuBottomBar(
                selected = Screens.Home,
                onItemSelected = { destination ->
                    coroutineScope.launch {
                        navigator.navigateTo(destination)
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(Theme.spacing.medium)
                    .testTag("home_fab"),
                onClick = { onSetupBlankPoll() },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text(stringResource(R.string.button_new_poll)) },
            )
        }
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Theme.spacing.medium)
                .verticalScroll(state = scrollState)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Theme.spacing.medium)
                    .semantics {
                        contentDescription = (context.getString(R.string.majority_judgment)
                                + " "
                                + context.getString(R.string.menu_home)
                                )
                    },
                fontSize = 32.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center,
                text = stringResource(R.string.title_multiline_majority_judgment_urn),
            )

            if (homeViewState.polls.isEmpty()) {
                Spacer(Modifier.size(Theme.spacing.large))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.incitation_making_new_poll),
                    fontStyle = FontStyle.Italic,
                )
            }

            // Not sure how to access the FAB with TalkBack
            Spacer(Modifier.height(Theme.spacing.medium))
            Button(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = { onSetupBlankPoll() },
            ) {
                Icon(Icons.Filled.Add, null)
                Text(stringResource(R.string.button_new_poll))
            }

            Spacer(Modifier.height(Theme.spacing.medium))

            homeViewState.polls.reversed().forEach { poll ->

                val showDeletionDialog = remember { mutableStateOf(false) }

                PollSummary(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Theme.spacing.small),
                    poll = poll,
                    onSetupClonePoll = { onSetupClonePoll(it) },
                    onResumePoll = { onResumePoll(it) },
                    onShowResult = { onShowResult(it) },
                    onDeletePoll = {
                        showDeletionDialog.value = true
                    },
                )
                Spacer(
                    Modifier
                        .padding(bottom = 48.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray),
                )

                if (showDeletionDialog.value) {
                    PollDeletionConfirmationDialog(
                        poll = poll,
                        onConfirm = {
                            showDeletionDialog.value = false
                            onDeletePoll(poll)
                        },
                        onDismiss = {
                            showDeletionDialog.value = false
                        },
                    )
                }
            }

            // Safe area : cause fab button
            Spacer(Modifier.height(72.dp))
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
@Suppress("SpellCheckingInspection")
fun PreviewHomeScreen(modifier: Modifier = Modifier) {
    JmTheme {
        HomeScreen(
            modifier = modifier,
            homeViewState = HomeViewModel.HomeViewState(
                polls = listOf(
                    Poll(
                        pollConfig = PollConfig(
                            subject = "President ?",
                            proposals = listOf("Mario", "Bob", "JLM"),
                            grading = DEFAULT_GRADING_QUALITY_VALUE,
                        ),
                        ballots = listOf(
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = 0, 2),
                                    Judgment(proposal = 1, 7),
                                    Judgment(proposal = 2, 1),
                                )
                            )
                        )
                    ),
                    Poll(
                        pollConfig = PollConfig(
                            subject = "De combien de megawatts la couleur bleu est-elle plus poilue que sous la mer ?",
                            proposals = listOf(
                                "42",
                                "Rose",
                                "Un sac de rats",
                                "Presque",
                                "Mercure",
                                "Un Sayan",
                                "Merde !"
                            ),
                            grading = DEFAULT_GRADING_QUALITY_VALUE,
                        ),
                        ballots = listOf(
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = 0, 2),
                                    Judgment(proposal = 1, 4),
                                ),
                            ),
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = 0, 1),
                                    Judgment(proposal = 1, 3),
                                ),
                            ),
                        )
                    )
                )
            )
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewHomeScreenWithEmptyPoll(modifier: Modifier = Modifier) {
    JmTheme {
        HomeScreen(
            modifier = modifier,
            homeViewState = HomeViewModel.HomeViewState(
                polls = emptyList(),
            )
        )
    }
}
