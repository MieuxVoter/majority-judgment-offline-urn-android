package com.illiouchine.jm.logic

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.service.DuelAnalyzer
import com.illiouchine.jm.service.ParticipantGroupAnalysis
import com.illiouchine.jm.service.TextStylist
import com.illiouchine.jm.ui.navigator.NavigationAction
import com.illiouchine.jm.ui.navigator.Screens
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ResultInterface
import fr.mieuxvoter.mj.TallyInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PollResultViewModel(
    private val pollDataSource: PollDataSource,
) : ViewModel() {

    data class PollResultViewState(
        val poll: Poll? = null,
        val tally: TallyInterface? = null,
        val result: ResultInterface? = null,
        val explanations: List<AnnotatedString> = emptyList(),
        val groups: List<DuelGroups> = emptyList(),
        val proportions: Map<ProportionalAlgorithms, List<Double>> = emptyMap(),
    )

    data class DuelGroups(
        val groups: List<ParticipantGroupAnalysis>,
    )

    private val _pollResultViewState = MutableStateFlow(PollResultViewState())
    val pollResultViewState: StateFlow<PollResultViewState> = _pollResultViewState

    private val _navEvents = MutableSharedFlow<NavigationAction>()
    val navEvents = _navEvents.asSharedFlow()

    fun initializePollResultById(context: Context, pollId: Int) {
        viewModelScope.launch {
            val poll = pollDataSource.getPollById(pollId)

            if (poll == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_that_poll_does_not_exist),
                    Toast.LENGTH_LONG,
                ).show()
                _navEvents.emit(NavigationAction.To(Screens.Home))
            } else {
                initializePollResult(context, poll)
            }
        }
    }

    fun initializePollResult(context: Context, poll: Poll) {
        val amountOfProposals = poll.pollConfig.proposals.size
        val amountOfGrades = poll.pollConfig.grading.getAmountOfGrades()
        val deliberation: DeliberatorInterface = MajorityJudgmentDeliberator()
        val tally = CollectedTally(amountOfProposals, amountOfGrades)

        poll.pollConfig.proposals.forEachIndexed { proposalIndex, _ ->
            val voteResult = poll.judgments.filter { it.proposal == proposalIndex }
            voteResult.forEach { judgment ->
                tally.collect(proposalIndex, judgment.grade)
            }
        }

        val result: ResultInterface = deliberation.deliberate(tally)

        val stylist = TextStylist()
        val groups: MutableList<DuelGroups> = mutableListOf()
        val explanations: MutableList<AnnotatedString> = mutableListOf()
        result.proposalResultsRanked.forEachIndexed { displayIndex, _ ->
            val otherIndex = if (displayIndex < amountOfProposals - 1) {
                displayIndex + 1
            } else {
                displayIndex - 1
            }
            val duelAnalyzer = DuelAnalyzer(
                poll = poll,
                tally = tally,
                result = result,
                baseIndex = displayIndex,
                otherIndex = otherIndex,
            )
            explanations.add(
                duelAnalyzer.generateDuelExplanation(
                    context = context,
                    stylist = stylist,
                )
            )
            groups.add(
                DuelGroups(
                    groups = duelAnalyzer.generateGroups(),
                )
            )
        }

        val proportions = mutableMapOf<ProportionalAlgorithms, List<Double>>()
        for (proportionalAlgorithm in ProportionalAlgorithms.entries) {
            if (proportionalAlgorithm.isAvailable()) {
                proportions[proportionalAlgorithm] = proportionalAlgorithm.compute(poll, result)
            }
        }

        _pollResultViewState.update {
            it.copy(
                poll = poll,
                tally = tally,
                result = result,
                explanations = explanations,
                groups = groups,
                proportions = proportions,
            )
        }
    }

    fun onFinish() {
        viewModelScope.launch {
            _navEvents.emit(NavigationAction.Clear)
        }
    }
}
