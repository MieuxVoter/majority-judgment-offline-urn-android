package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.R
import com.illiouchine.jm.Screens
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.composable.PollSummary
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewState: HomeViewModel.HomeViewState = HomeViewModel.HomeViewState(),
    navController: NavController = rememberNavController(),
    onSetupBlankPoll: () -> Unit = {},
    onSetupClonePoll: (poll: Poll) -> Unit = {},
    onResumePoll: (poll: Poll) -> Unit = {},
    onShowResult: (poll: Poll) -> Unit = {},
    onDeletePoll: (poll: Poll) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            MjuBottomBar(
                selected = navController.currentDestination?.route ?: Screens.Home.name,
                onItemSelected = { destination -> navController.navigate(destination.id) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = { onSetupBlankPoll() },
                icon = { Icon(Icons.Filled.Add, "+") },
                text = { Text(text = stringResource(R.string.button_new_poll)) },
            )
        }
    ) { innerPadding ->

        //Todo : Manage scroll only polls history
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(state = ScrollState(initial = 0))
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                fontSize = 8.em,
                textAlign = TextAlign.Center,
                lineHeight = 1.3.em,
                text = stringResource(R.string.title_multiline_majority_judgment_urn),
            )

            if (homeViewState.polls.isEmpty()) {
                Spacer(Modifier.size(36.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.incitation_making_new_poll),
                    fontStyle = FontStyle.Italic,
                )
            }

            Spacer(Modifier.height(16.dp))

            homeViewState.polls.reversed().forEach { poll ->
                PollSummary(
                    modifier = Modifier.fillMaxWidth().padding(top=8.dp),
                    poll = poll,
                    onSetupClonePoll = { onSetupClonePoll(it) },
                    onResumePoll = { onResumePoll(it) },
                    onShowResult = { onShowResult(it) },
                    onDeletePoll = { onDeletePoll(it) },
                )
                Spacer(
                    Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color.LightGray)
                )
            }
            // Safe area : cause fab button
            Spacer(Modifier.height(72.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewHomeScreen(modifier: Modifier = Modifier) {
    JmTheme {
        HomeScreen(
            homeViewState = HomeViewModel.HomeViewState(
                polls = listOf(
                    Poll(
                        pollConfig = PollConfig(
                            subject = "Prezidan ?",
                            proposals = listOf("Mario", "Bob", "JLM"),
                            grading = Grading.Quality7Grading
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
                            grading = Grading.Quality7Grading,
                        ),
                        ballots = listOf(
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = 0, 2),
                                    Judgment(proposal = 1, 7),
                                ),
                            ),
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = 0, 1),
                                    Judgment(proposal = 1, 6),
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
            homeViewState = HomeViewModel.HomeViewState(
                polls = emptyList(),
            )
        )
    }
}