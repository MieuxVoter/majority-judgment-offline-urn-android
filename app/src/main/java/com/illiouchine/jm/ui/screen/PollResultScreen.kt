package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.ProportionalAlgorithms
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.DefaultNavigator
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.composable.BallotCountRow
import com.illiouchine.jm.ui.composable.LinearMeritProfileCanvas
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import com.illiouchine.jm.ui.utils.smoothStep
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    state: PollResultViewModel.PollResultViewState,
    navigator: Navigator = DefaultNavigator(),
    onFinish: () -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    val poll = state.poll!!
    val result = state.result!!
    val tally = state.tally!!
    val grading = poll.pollConfig.grading

    var isAnyProfileSelected by remember { mutableStateOf(false) }
    var selectedProfileIndex by remember { mutableIntStateOf(0) }
    // Not all these groups belong to the selected profile ; they belong to a duel
    val decisiveGroupsForSelectedProfile = state.groups[selectedProfileIndex].groups

    var proportionalDropdownExpanded by remember { mutableStateOf(false) }
    var proportionalAlgorithm by remember { mutableStateOf(ProportionalAlgorithms.NONE) }

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

        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = Theme.spacing.small)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(Theme.spacing.small),
        ) {

            PollSubject(
                modifier = Modifier.padding(bottom = Theme.spacing.small + Theme.spacing.medium),
                subject = poll.pollConfig.subject,
            )

            BallotCountRow(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                ballots = poll.ballots,
            )

            Spacer(Modifier.padding(vertical = Theme.spacing.small))

            val appearAnimation = remember { Animatable(0f) }
            LaunchedEffect("waterfall") {
                appearAnimation.animateTo(1f, tween(1500))
            }

            val amountOfProposals = result.proposalResultsRanked.size
            result.proposalResultsRanked.forEachIndexed { displayIndex, proposalResult ->
                if (proposalResult.analysis.totalSize > BigInteger.ZERO) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                // Clicking on the last is clicking on the penultimate.
                                val clickedIndex = if (displayIndex == amountOfProposals - 1) {
                                    displayIndex - 1
                                } else {
                                    displayIndex
                                }
                                // Behaves like an exclusive toggle
                                if (isAnyProfileSelected && selectedProfileIndex == clickedIndex) {
                                    isAnyProfileSelected = false
                                } else {
                                    isAnyProfileSelected = true
                                    selectedProfileIndex = clickedIndex
                                }
                            }
                            .alpha(
                                smoothStep(
                                    max(0f, 0.85f * displayIndex / amountOfProposals),
                                    min(1f, 1.15f * (displayIndex + 1) / amountOfProposals),
                                    appearAnimation.value,
                                )
                            ),

                        ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            val rank = proposalResult.rank
                            val proposalName = poll.pollConfig.proposals[proposalResult.index]
                            val medianGrade = proposalResult.analysis.medianGrade
                            val medianGradeName =
                                stringResource(poll.pollConfig.grading.getGradeName(medianGrade))
                            Text(
                                modifier = Modifier
                                    .padding(end = Theme.spacing.extraSmall + Theme.spacing.small),
                                fontSize = 24.sp,
                                text = "#$rank",
                            )
                            var proportionAsText = ""
                            if (proportionalAlgorithm != ProportionalAlgorithms.NONE) {
                                val shownProportions = state.proportions[proportionalAlgorithm]
                                if (shownProportions != null) {
                                    proportionAsText = String.format(
                                        Locale.FRANCE,
                                        "   %.1f%%",
                                        100 * shownProportions[proposalResult.index],
                                    )
                                }
                            }
                            Text(
                                text = "$proposalName   ($medianGradeName)$proportionAsText",
                            )
                        }

                        Row {
                            LinearMeritProfileCanvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Theme.spacing.medium + Theme.spacing.small),
                                tally = tally,
                                proposalResult = proposalResult,
                                grading = grading,
                                decisiveGroups = decisiveGroupsForSelectedProfile.filter { group ->
                                    group.participant == displayIndex
                                },
                                showDecisiveGroups = isAnyProfileSelected,
                            )
                        }

                        Spacer(
                            Modifier.padding(
                                vertical = Theme.spacing.small + Theme.spacing.tiny,
                            )
                        )

                        // Ux: Explanations are shown one at a time (exclusive toggle)
                        val shouldShowExplanation = isAnyProfileSelected
                                && selectedProfileIndex == displayIndex

                        AnimatedVisibility(shouldShowExplanation) {
                            Row {
                                Text(
                                    fontSize = 14.sp,
                                    text =
                                    if (state.explanations.size > displayIndex) {
                                        state.explanations[displayIndex]
                                    } else {
                                        AnnotatedString("\uD83D\uDC1E")
                                    },
                                )
                            }
                        }

                        Spacer(Modifier.padding(vertical = Theme.spacing.tiny))
                    }
                }
            }

            Spacer(modifier = Modifier.padding(Theme.spacing.small))

            Row {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                    textAlign = TextAlign.End,
                    text = stringResource(R.string.label_show_proportions) + ":",

                )

                Box {
                    TextButton(
                        onClick = {
                            proportionalDropdownExpanded = !proportionalDropdownExpanded
                        },
                    ) {
                        Text(proportionalAlgorithm.getName(LocalContext.current))
                    }

                    DropdownMenu(
                        expanded = proportionalDropdownExpanded,
                        onDismissRequest = { proportionalDropdownExpanded = false }
                    ) {
                        for (algo in ProportionalAlgorithms.entries) {
                            DropdownMenuItem(
                                enabled = algo.isAvailable(),
                                text = { Text(algo.getName(LocalContext.current)) },
                                onClick = {
                                    proportionalAlgorithm = algo
                                    proportionalDropdownExpanded = false
                                },
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            navigator.navigateTo(
                                destination = Screens.ProportionsHelp,
                            )
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "More Info",
                    )
                }
            }

            Spacer(modifier = Modifier.padding(Theme.spacing.small))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onFinish,
            ) { Text(stringResource(R.string.button_finish)) }
        }
    }
}

// To correctly preview this, you need to Start Interactive Mode.
// This is the cost of animating the apparition of the merit profiles.
@Preview(
    name = "Phone (Portrait)",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Preview(
    name = "Phone (Portrait, Big Font)",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 2.0f,
)
@Preview(
    name = "Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
//@PreviewScreenSizes // my eyes hurt ← no dark mode
@Composable
fun PreviewResultScreen(modifier: Modifier = Modifier) {

    val poll = Poll(
        pollConfig = PollConfig(
            subject = "Epic Plumbers",
            proposals = listOf(
                "Luigi the green plumber with a mustache and a long name, mamma mia !",
                "Bob",
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
                    Judgment(2, 5),
                )
            ),
        ),
    )
    val pollResultViewModel = PollResultViewModel(
        navigator = DefaultNavigator(),
        pollDataSource = InMemoryPollDataSource(), // dummy
    )
    pollResultViewModel.initializePollResult(LocalContext.current, poll)
    val state = pollResultViewModel.pollResultViewState.collectAsState().value

    JmTheme {
        ResultScreen(
            modifier = modifier,
            state = state,
        )
    }
}
