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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.ProportionalAlgorithms
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.BallotCountRow
import com.illiouchine.jm.ui.composable.LinearMeritProfileCanvas
import com.illiouchine.jm.ui.composable.MjuSnackbar
import com.illiouchine.jm.ui.composable.PollSubject
import com.illiouchine.jm.ui.previewdatabuilder.PreviewDataBuilder
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import com.illiouchine.jm.ui.utils.smoothStep
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Locale
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    state: PollResultViewModel.PollResultViewState,
    onShowProportionsHelp: () -> Unit = {},
    onFinish: () -> Unit = {},
    feedback: String? = "",
    onDismissFeedback: () -> Unit = {},
) {
    val poll = state.poll!!
    val result = state.result!!
    val tally = state.tally!!
    val grading = poll.pollConfig.grading

    val context = LocalContext.current
    val amountOfProposals = result.proposalResultsRanked.size

    var isAnyProfileSelected by remember { mutableStateOf(false) }
    var selectedProfileIndex by remember { mutableIntStateOf(0) }
    // Selecting the last proposal behaves like selecting the penultimate, duel-wise.
    val selectedDuelIndex = if (selectedProfileIndex == amountOfProposals - 1) {
        selectedProfileIndex - 1
    } else {
        selectedProfileIndex
    }

    // Not all these groups belong to the selected profile ; they belong to a duel
    val decisiveGroupsForSelectedProfile = state.groups[selectedDuelIndex].groups

    var proportionalDropdownExpanded by remember { mutableStateOf(false) }
    var proportionalAlgorithm by rememberSaveable { mutableStateOf(ProportionalAlgorithms.NONE) }

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

            result.proposalResultsRanked.forEachIndexed { proposalDisplayIndex, proposalResult ->
                if (proposalResult.analysis.totalSize > BigInteger.ZERO) {
                    // I don't know how to indent this properly (I am triggered)
                    val isInSelectedDuel = isAnyProfileSelected &&
                            (selectedDuelIndex == proposalDisplayIndex
                                    || selectedDuelIndex + 1 == proposalDisplayIndex)
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
                                    @Suppress("AssignedValueIsNeverRead")  // because it IS
                                    selectedProfileIndex = proposalDisplayIndex
                                }
                            }
                            .semantics {
                                if (isInSelectedDuel) {
                                    // Bit of a hack to force reading the explanations that show up.
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
                                modifier = Modifier
                                    .padding(end = Theme.spacing.extraSmall + Theme.spacing.small),
                                fontSize = 24.sp,
                                text = "#$rank",
                            )
                            var proportionAsText = ""
                            if (proportionalAlgorithm != ProportionalAlgorithms.NONE) {
                                val shownProportions = state.proportions[proportionalAlgorithm]
                                if (shownProportions != null) {
                                    proportionAsText = "   " + formatAmount(
                                        amount = 100 * shownProportions[proposalResult.index],
                                        maxDecimals = 3,
                                    ) + "%"
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
                                    group.participant == proposalDisplayIndex
                                }.toImmutableList(),
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
                                && selectedDuelIndex == proposalDisplayIndex

                        AnimatedVisibility(shouldShowExplanation) {
                            Row {
                                Text(
                                    fontSize = 14.sp,
                                    text =
                                        if (state.explanations.size > proposalDisplayIndex) {
                                            state.explanations[proposalDisplayIndex]
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = stringResource(R.string.label_show_proportions) + ":",
                )

                Box {
                    val ttsChooseAProportionalAlgorithm = stringResource(
                        R.string.tts_choose_a_proportional_algorithm
                    )
                    TextButton(
                        modifier = Modifier
                            .semantics {
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
                        onDismissRequest = { proportionalDropdownExpanded = false }
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

            Spacer(modifier = Modifier.padding(Theme.spacing.small))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onFinish,
            ) { Text(stringResource(R.string.button_finish)) }
        }
    }
}

fun formatAmount(amount: Double, maxDecimals: Int = 2, locale: Locale = Locale.FRANCE): String {
    var decimals = 0
    while (
        decimals < maxDecimals
        &&
        floor(amount * 10.0.pow(decimals)) != amount * 10.0.pow(decimals)
    ) {
        decimals += 1
    }
    return String.format(locale, "%.${decimals}f", amount)
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

    val poll = PreviewDataBuilder.poll()

    val pollResultViewModel = viewModel {
        PollResultViewModel(
            pollDataSource = InMemoryPollDataSource(), // dummy
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
