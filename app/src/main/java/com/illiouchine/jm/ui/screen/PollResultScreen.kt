package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.config.ProportionalAlgorithms
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.extensions.bigSumOf
import com.illiouchine.jm.extensions.smartFormat
import com.illiouchine.jm.filters.BallotsFilterInterface
import com.illiouchine.jm.filters.NoBallotsFilter
import com.illiouchine.jm.filters.NuanceBallotsFilter
import com.illiouchine.jm.filters.ProposalGradeBallotsFilter
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.model.ProposalTally
import com.illiouchine.jm.ui.composable.BallotCountRow
import com.illiouchine.jm.ui.composable.LinearMeritProfileCanvas
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.composable.plot.NuanceProfile
import com.illiouchine.jm.ui.composable.plot.OpinionProfileBarChart
import com.illiouchine.jm.ui.composable.plot.ProximityBarChart
import com.illiouchine.jm.ui.composable.plot.ProximitySpider
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.plot.utils.filterAnalysis
import com.illiouchine.jm.ui.composable.scaffold.MjuScaffold
import com.illiouchine.jm.ui.composable.scaffold.MjuSnackbar
import com.illiouchine.jm.ui.composable.spacer.MediumVerticalSpacer
import com.illiouchine.jm.ui.composable.spacer.SmallVerticalSpacer
import com.illiouchine.jm.ui.preview.PreviewDataFaker
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import com.illiouchine.jm.ui.utils.smoothStep
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    state: PollResultViewModel.PollResultViewState,
    onShowProportionsHelp: () -> Unit = {},
    onFinish: () -> Unit = {},
    onBallotsFilterUpdate: (BallotsFilterInterface) -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    val unfilteredPoll = state.unfilteredPoll!!
    val ballotsFilter = state.ballotFilter
    val poll = state.poll!!
    val result = state.result!!
    val tally = state.tally!!
    val grading = poll.pollConfig.grading
    val highestGradeToLowestGrade = state.highGradeOnLeft

    val context = LocalContext.current
    val amountOfProposals = result.proposalResultsRanked.size
    val amountOfBallots = poll.ballots.size

    var isAnyProfileSelected by remember { mutableStateOf(false) }
    var selectedProfileIndex by remember { mutableIntStateOf(0) }
    // Selecting the last proposal behaves like selecting the penultimate, duel-wise.
    val selectedDuelIndex = if (selectedProfileIndex == amountOfProposals - 1) {
        max(0, selectedProfileIndex - 1)
    } else {
        selectedProfileIndex
    }

    // Not all these groups belong to the selected profile ; they belong to a duel
    val decisiveGroupsForSelectedDuel = state.groups[selectedDuelIndex].groups

    var proportionalDropdownExpanded by remember { mutableStateOf(false) }
    var proportionalAlgorithm by rememberSaveable { mutableStateOf(ProportionalAlgorithms.NONE) }

    var ballotsFiltersExpanded by rememberSaveable { mutableStateOf(false) }
    var newBallotsFilterDropdownExpanded by remember { mutableStateOf(false) }

    MjuScaffold(
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
                ballots = poll.ballots.toPersistentList(),
                unfilteredBallots = unfilteredPoll.ballots.toPersistentList(),
                ballotsFilter = ballotsFilter,
                onClick = {
                    ballotsFiltersExpanded = !ballotsFiltersExpanded
                }
            )

            if (ballotsFiltersExpanded) {
                // Rule: for simplicity, for now, only one filter is allowed.
                // Eventually; we'd love a filters tree (AND/OR) like in Factorio for example.

                SmallVerticalSpacer()

                if (ballotsFilter is NoBallotsFilter) {
                    Text("No filter is applied on the ballots.")

                    // The purpose of this Column is to position the DropdownMenu adequately.
                    // Without it, it appears at the bottom of the screen, which is weird.
                    Column {
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                newBallotsFilterDropdownExpanded = !newBallotsFilterDropdownExpanded
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add a filter.",
                            )
                        }

                        DropdownMenu(
                            expanded = newBallotsFilterDropdownExpanded,
                            onDismissRequest = {
                                newBallotsFilterDropdownExpanded = false
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Filter by grade")
                                },
                                onClick = {
                                    newBallotsFilterDropdownExpanded = false
                                    onBallotsFilterUpdate(
                                        ProposalGradeBallotsFilter(
                                            proposalIndex = 0,
                                            gradeIndex = 0,
                                            comparatorIndex = 1,
                                        )
                                    )
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Filter by nuance")
                                },
                                onClick = {
                                    newBallotsFilterDropdownExpanded = false
                                    onBallotsFilterUpdate(
                                        NuanceBallotsFilter(
                                            comparatorIndex = 1,
                                            nuance = 1,
                                        )
                                    )
                                },
                            )
                        }
                    }
                } else {
                    Text("Now only using the ballots that:")
                    ballotsFilter.render(
                        poll = poll,
                        onFilterDelete = {
                            // Mmh ; perhaps add a onBallotFilterDelete upstream ?
                            onBallotsFilterUpdate(NoBallotsFilter())
                        },
                        onFilterUpdate = {
                            onBallotsFilterUpdate(it)
                        },
                    ).invoke()
                }
            }
            SmallVerticalSpacer()

            val appearAnimation = remember { Animatable(0f) }
            LaunchedEffect("waterfall") {
                appearAnimation.animateTo(1f, tween(1500))
            }

            result.proposalResultsRanked.forEachIndexed { proposalDisplayIndex, proposalResult ->
                if (proposalResult.analysis.totalSize > BigInteger.ZERO) {
                    // I don't know how to indent this readably (I am triggered by the linter >.<)
                    val isInSelectedDuel = isAnyProfileSelected && (
                        selectedDuelIndex == proposalDisplayIndex ||
                            selectedDuelIndex + 1 == proposalDisplayIndex
                        )
                    val ttsShowExplanation = stringResource(R.string.tts_show_explanation)

                    Column(
                        modifier = Modifier
                            .alpha(
                                smoothStep(
                                    max(0f, 0.85f * proposalDisplayIndex / amountOfProposals),
                                    min(1f, 1.15f * (proposalDisplayIndex + 1) / amountOfProposals),
                                    appearAnimation.value,
                                ) * (if (isInSelectedDuel || !isAnyProfileSelected) 1.0f else 0.38f)
                            )
                            .clickable {
                                if (isInSelectedDuel) {
                                    isAnyProfileSelected = false
                                } else {
                                    isAnyProfileSelected = true
                                    @Suppress("AssignedValueIsNeverRead") // because it IS
                                    selectedProfileIndex = proposalDisplayIndex
                                }
                            }
                            .semantics {
                                if (isInSelectedDuel) {
                                    // Hack to force reading the explanations that show up.
                                    // NOTE: does not work well on the last merit profile.
                                    liveRegion = LiveRegionMode.Assertive
                                }
                                onClick(label = ttsShowExplanation) {
                                    true
                                }
                            },
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            val rank = proposalResult.rank
                            val proposalName = poll.pollConfig.proposals[proposalResult.index]
                            val medianGrade = proposalResult.analysis.medianGrade
                            val medianGradeName = stringResource(
                                poll.pollConfig.grading.getGradeName(medianGrade)
                            )
                            Text(
                                modifier = Modifier.padding(end = Theme.spacing.extraSmall + Theme.spacing.small),
                                fontSize = 24.sp,
                                text = "#$rank",
                            )
                            var proportionAsText = ""
                            if (proportionalAlgorithm != ProportionalAlgorithms.NONE) {
                                val shownProportions = state.proportions[proportionalAlgorithm]
                                if (shownProportions != null) {
                                    proportionAsText = String.format(
                                        Locale.FRANCE,
                                        "   %s%%",
                                        (100 * shownProportions[proposalResult.index]).smartFormat(
                                            maxDecimals = 2,
                                        )
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
                                    .height(Theme.spacing.large),
                                proposalTally = tally.proposalsTallies[proposalResult.index],
                                medianGrade = proposalResult.analysis.medianGrade,
                                grading = grading,
                                decisiveGroups = decisiveGroupsForSelectedDuel.filter { group ->
                                    group.participant == proposalDisplayIndex
                                }.toPersistentList(),
                                showDecisiveGroups = isAnyProfileSelected,
                                highestGradeOnTheLeft = highestGradeToLowestGrade,
                            )
                        }

                        Spacer(
                            Modifier.padding(
                                vertical = Theme.spacing.small + Theme.spacing.tiny,
                            )
                        )

                        // Ux: Explanations are shown one at a time (exclusive toggle)
                        val shouldShowExplanation = isAnyProfileSelected &&
                            selectedDuelIndex == proposalDisplayIndex &&
                            result.proposalResultsRanked.size > 1

                        AnimatedVisibility(shouldShowExplanation) {
                            Row {
                                Text(
                                    fontSize = 14.sp,
                                    text = if (state.explanations.size > proposalDisplayIndex) {
                                        state.explanations[proposalDisplayIndex]
                                    } else {
                                        AnnotatedString("\uD83D\uDC1E")
                                    },
                                )
                            }
                        }

                        Spacer(Modifier.padding(vertical = Theme.spacing.small))
                    }
                }
            }

            SmallVerticalSpacer()

            if (amountOfBallots > 0) {
                Row {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(R.string.label_show_proportions) + ":",
                    )

                    Box {
                        val ttsChooseAProportionalAlgorithm = stringResource(
                            R.string.tts_choose_a_proportional_algorithm
                        )
                        TextButton(
                            modifier = Modifier.semantics {
                                onClick(
                                    label = ttsChooseAProportionalAlgorithm,
                                    action = null,
                                )
                            },
                            onClick = {
                                proportionalDropdownExpanded = !proportionalDropdownExpanded
                            },
                        ) {
                            Text(proportionalAlgorithm.getName(context))
                        }

                        DropdownMenu(
                            expanded = proportionalDropdownExpanded,
                            onDismissRequest = {
                                proportionalDropdownExpanded = false
                            },
                        ) {
                            for (algo in ProportionalAlgorithms.entries) {
                                DropdownMenuItem(
                                    modifier = Modifier.semantics {
                                        contentDescription = "Proportional Algorithm"
                                    },
                                    enabled = algo.isAvailable(),
                                    text = { Text(algo.getName(context)) },
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
                                onShowProportionsHelp()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(
                                R.string.tts_more_information_about_proportional_algorithms
                            ),
                        )
                    }
                }
                MediumVerticalSpacer()
            }

            if (amountOfBallots > 0) {
                Text(stringResource(R.string.opinion_profile))
                SmallVerticalSpacer()

                val pollTallyAsProposalTally = ProposalTally(
                    tally = List(grading.grades.size) { gradeIndex ->
                        tally.proposalsTallies.bigSumOf { it.tally[gradeIndex] }
                    }.toPersistentList(),
                    amountOfJudgments = tally.proposalsTallies.bigSumOf { it.amountOfJudgments },
                )

                LinearMeritProfileCanvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Theme.spacing.medium + Theme.spacing.small),
                    proposalTally = pollTallyAsProposalTally,
                    grading = grading,
                    highestGradeOnTheLeft = highestGradeToLowestGrade,
                )
                MediumVerticalSpacer()

                OpinionProfileBarChart(
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth(),
                    poll = poll,
                    tally = tally,
                    highestGradeToLowestGrade = highestGradeToLowestGrade,
                )
                PlotTitle(
                    text = stringResource(R.string.plot_title_opinion_profile),
                )
                MediumVerticalSpacer()
            }

            if (amountOfBallots > 0) {
                Text(stringResource(R.string.nuance_profile))
                SmallVerticalSpacer()
                NuanceProfile(
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth(),
                    poll = poll,
                    moreNuanceToLessNuance = highestGradeToLowestGrade,
                )
                PlotTitle(
                    modifier = Modifier.padding(top = Theme.spacing.tiny),
                    text = stringResource(R.string.plot_title_nuance_profile),
                )
                MediumVerticalSpacer()
            }

            // Rule: hide the proximity profile if there's only one proposal, as it's useless.
            if (amountOfProposals > 1 && amountOfBallots > 0) {
                val partialProximityAnalysis = filterAnalysis(
                    state.proximityAnalysis!!,
                    result.proposalResultsRanked.map { it.index },
                )
                var selectedPartialAnalysisProposal by remember { mutableIntStateOf(0) }

                Text(stringResource(R.string.proximity_profile))
                SmallVerticalSpacer()
                ProximityBarChart(
                    modifier = Modifier
                        .height(
                            16.dp *
                                partialProximityAnalysis.proposals.size *
                                partialProximityAnalysis.proposals.size
                        )
                        .fillMaxWidth(),
                    analysis = partialProximityAnalysis,
                )
                PlotTitle(
                    text = stringResource(R.string.plot_title_proximity_profile),
                )
                MediumVerticalSpacer()

                // The purpose of this toast is to show the full proposal name when we select one.
                val rememberedContext = remember { context } // not 100% sure we need this
                var proposalNameToast by remember {
                    mutableStateOf(
                        Toast.makeText(
                            rememberedContext,
                            "",
                            Toast.LENGTH_SHORT,
                        )
                    )
                }

                // Rule: hide the proximity spider if there's less than three proposals (buggy!)
                if (amountOfProposals > 2) {
                    ProximitySpider(
                        modifier = Modifier.height(400.dp),
                        analysis = partialProximityAnalysis,
                        selectedProposalIndex = selectedPartialAnalysisProposal,
                        onProposalSelected = {
                            selectedPartialAnalysisProposal = it

                            proposalNameToast.cancel()
                            // We can't use setText here, perhaps because cancel hasn't finished yet
                            // proposalNameToast.setText(partialProximityAnalysis.proposals[it])
                            // But creating a new Toast instance entirely works
                            proposalNameToast = Toast.makeText(
                                rememberedContext,
                                partialProximityAnalysis.proposals[it],
                                Toast.LENGTH_SHORT,
                            )
                            proposalNameToast.show()
                        },
                    )
                    MediumVerticalSpacer()
                }
            }

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onFinish,
            ) { Text(stringResource(R.string.button_finish)) }
        }
    }
}

// To correctly preview this, you need to Start Interactive Mode.
// This is a hidden cost of animating the apparition of the merit profiles.
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
// @PreviewScreenSizes // my eyes hurt ← no dark mode ; but we should cook something like this
@Composable
fun PreviewResultScreen(modifier: Modifier = Modifier) {
    val poll = PreviewDataFaker.poll(
        amountOfBallots = 10000,
        amountOfProposals = 7,
        tweakBallots = { _, ballot, _ ->
            ballot.copy(
                judgments = ballot.judgments.map {
                    when (it.proposal) {
                        0 -> {
                            it.copy(grade = 0)
                        }

                        1 -> {
                            it.copy(grade = 1)
                        }

                        2 -> {
                            it.copy(grade = 2)
                        }

                        3 -> {
                            it.copy(grade = 3)
                        }

                        4 -> {
                            it.copy(grade = 4)
                        }

                        5 -> {
                            it
                        }

                        else -> {
                            it
                        }
                    }
                }.toPersistentList()
            )
        },
        nameProposals = {
            when (it) {
                0 -> {
                    "Reject"
                }

                1 -> {
                    "Insufficient"
                }

                2 -> {
                    "Passable"
                }

                3 -> {
                    "Good"
                }

                4 -> {
                    "Excellent"
                }

                5 -> {
                    "Random 1"
                }

                else -> {
                    "Random 2"
                }
            }
        }
    )

    val context = LocalContext.current
    val pollResultViewModel = viewModel {
        PollResultViewModel(
            pollDataSource = InMemoryPollDataSource(), // dummy
            sharedPrefsHelper = SharedPrefsHelper(context),
        )
    }
    pollResultViewModel.initializePollResult(LocalContext.current, poll)
    val state = pollResultViewModel.pollResultViewState.collectAsState().value

    JmTheme {
        ResultScreen(
            modifier = modifier,
            state = state,
        )
    }
}

@Preview(
    name = "Phone (Portrait)",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 2.0f,
)
@Composable
fun PreviewFilteredResultScreen(modifier: Modifier = Modifier) {
    val poll = PreviewDataFaker.poll(
        amountOfBallots = 1000,
        amountOfProposals = 5,
        tweakBallots = { _, ballot, _ ->
            ballot.copy(
                judgments = ballot.judgments.map {
                    when (it.proposal) {
                        0 -> {
                            it.copy(grade = 0)
                        }

                        1 -> {
                            it.copy(grade = 1)
                        }

                        2 -> {
                            it.copy(grade = 4)
                        }

                        else -> {
                            it
                        }
                    }
                }.toPersistentList()
            )
        },
        nameProposals = {
            when (it) {
                0 -> {
                    "Reject"
                }

                1 -> {
                    "Passable"
                }

                2 -> {
                    "Excellent"
                }

                3 -> {
                    "Random A"
                }

                else -> {
                    "Random B"
                }
            }
        }
    )

    val ballotsFilter: BallotsFilterInterface = remember {
        ProposalGradeBallotsFilter(
            proposalIndex = 3,
            gradeIndex = 0,
            comparatorIndex = 1,
        )
    }

    val context = LocalContext.current
    val pollResultViewModel = viewModel {
        PollResultViewModel(
            pollDataSource = InMemoryPollDataSource(), // dummy
            sharedPrefsHelper = SharedPrefsHelper(context),
        )
    }

//    LaunchedEffect(poll, ballotsFilter) { // → preview refresh loop
    pollResultViewModel.initializePollResult(
        context = context,
        poll = poll,
        ballotFilter = ballotsFilter,
    )
//    }

    val state = pollResultViewModel.pollResultViewState.collectAsState().value

    JmTheme {
        if (state.poll != null) {
            ResultScreen(
                modifier = modifier,
                state = state,
                onBallotsFilterUpdate = {
                    // ballotsFilter = it
                },
            )
        }
    }
}
