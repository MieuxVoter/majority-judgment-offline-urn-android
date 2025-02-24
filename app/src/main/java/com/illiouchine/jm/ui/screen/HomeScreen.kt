package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.Screens
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewState: HomeViewModel.HomeViewState = HomeViewModel.HomeViewState(),
    navController: NavController = rememberNavController(),
    onSetupBlankPoll: () -> Unit = {},
    onSetupClonePoll: (poll: Poll) -> Unit = {},
    onVoteClonePoll: (poll: Poll) -> Unit = {},
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
                modifier = Modifier.padding(24.dp),
                onClick = { onSetupBlankPoll() },
                icon = { Icon(Icons.Filled.Add, "+") },
                text = { Text(text = "New Poll") },
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
                text = "Majority\nJudgment\nUrn",
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Try making a new poll, it's free !",
                fontStyle = FontStyle.Italic,
            )

            Spacer(Modifier.size(36.dp))

            if (homeViewState.polls.isNotEmpty()){
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Polls history",
                )
            }
            Spacer(Modifier.size(16.dp))

            homeViewState.polls.forEach { poll ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            modifier = Modifier,
                            text = poll.pollConfig.subject,
                        )
                        Row() {
                            Text(
                                modifier = Modifier.wrapContentSize(),
                                text = "Proposals : ${poll.pollConfig.proposals.size}"
                            )
                            Text(
                                modifier = Modifier,
                                text = "Ballots : ${poll.ballots.size}"
                            )
                            Button(onClick = { onDeletePoll(poll) }) {
                                Text("Delete")
                            }
                        }
                        Row {
                            Button(onClick = { onSetupClonePoll(poll) }) {
                                Text("Setup again")
                            }
                            Button(onClick = { onVoteClonePoll(poll) }) {
                                Text("Vote Again")
                            }
                            Button(onClick = { onShowResult(poll) }) {
                                Text("Show Result")
                            }
                        }
                    }
                }
            }
            // Safe area : cause fab button
            Spacer(Modifier.size(72.dp))
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
                            grading = Quality7Grading()
                        ),
                        ballots = listOf(
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = "Mario", 2),
                                    Judgment(proposal = "Bob", 7),
                                    Judgment(proposal = "JLM", 1),
                                )
                            )
                        )
                    ),
                    Poll(
                        pollConfig = PollConfig(
                            subject = "Ouiiiiiii ?",
                            proposals = listOf("Oui", "Non"),
                            grading = Quality7Grading()
                        ),
                        ballots = listOf(
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = "Oui", 2),
                                    Judgment(proposal = "Non", 7),
                                ),
                            ),
                            Ballot(
                                judgments = listOf(
                                    Judgment(proposal = "Oui", 1),
                                    Judgment(proposal = "Non", 6),
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
                polls = emptyList()
            )
        )
    }
}