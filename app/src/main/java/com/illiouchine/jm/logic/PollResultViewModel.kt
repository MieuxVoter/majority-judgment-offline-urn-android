package com.illiouchine.jm.logic

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.config.DEFAULT_HIGH_GRADE_ON_LEFT_VALUE
import com.illiouchine.jm.config.ProportionalAlgorithms
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.filters.BallotsFilterInterface
import com.illiouchine.jm.filters.NoBallotsFilter
import com.illiouchine.jm.model.ParticipantGroupAnalysis
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.Result
import com.illiouchine.jm.model.Tally
import com.illiouchine.jm.model.toResult
import com.illiouchine.jm.model.toTally
import com.illiouchine.jm.service.DuelAnalyzer
import com.illiouchine.jm.service.InnocentHand
import com.illiouchine.jm.service.ProximityAnalysis
import com.illiouchine.jm.service.ProximityAnalyzer
import com.illiouchine.jm.service.TextStylist
import com.illiouchine.jm.ui.navigator.NavigationAction
import com.illiouchine.jm.ui.navigator.Screens
import fr.mieuxvoter.kmj.DeliberatorInterface
import fr.mieuxvoter.kmj.MajorityJudgment
import fr.mieuxvoter.kmj.tally.CollectedPollTally
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class PollResultViewModel(
    private val pollDataSource: PollDataSource,
    private val sharedPrefsHelper: SharedPrefsHelper,
) : ViewModel() {

    @Stable
    data class PollResultViewState(
        val poll: Poll? = null,
        val tally: Tally? = null,
        val result: Result? = null,
        val explanations: List<AnnotatedString> = emptyList(),
        val groups: List<DuelGroups> = emptyList(),
        val proportions: Map<ProportionalAlgorithms, List<Double>> = emptyMap(),
        val lottery: Map<ProportionalAlgorithms, List<Int>> = emptyMap(),
        val proximityAnalysis: ProximityAnalysis? = null,
        val ballotFilter: BallotsFilterInterface = NoBallotsFilter(),
        val unfilteredPoll: Poll? = null,
        val highGradeOnLeft: Boolean = DEFAULT_HIGH_GRADE_ON_LEFT_VALUE,
    )

    @Stable
    data class DuelGroups(
        val groups: List<ParticipantGroupAnalysis>,
    )

    private val _viewState = MutableStateFlow(PollResultViewState())
    val viewState: StateFlow<PollResultViewState> = _viewState

    private val _navEvents = MutableSharedFlow<NavigationAction>()
    val navEvents = _navEvents.asSharedFlow()

    fun initializePollResultById(
        context: Context,
        pollId: Int,
        ballotFilter: BallotsFilterInterface = NoBallotsFilter(),
    ) {
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
                initializePollResult(
                    context = context,
                    poll = poll,
                    ballotFilter = ballotFilter,
                )
            }
        }
    }

    fun initializePollResult(
        context: Context,
        poll: Poll,
        ballotFilter: BallotsFilterInterface = NoBallotsFilter(),
    ) {
        val filteredPoll = poll.copy(
            ballots = poll.ballots.filter {
                ballotFilter.shouldKeep(it)
            },
        )
        val amountOfProposals = filteredPoll.pollConfig.proposals.size
        val amountOfGrades = filteredPoll.pollConfig.grading.getAmountOfGrades()
        val deliberation: DeliberatorInterface = MajorityJudgment()
        val tally = CollectedPollTally(amountOfProposals, amountOfGrades)

        filteredPoll.pollConfig.proposals.forEachIndexed { proposalIndex, _ ->
            val voteResult = filteredPoll.judgments.filter { it.proposal == proposalIndex }
            voteResult.forEach { judgment ->
                tally.collect(candidateIndex = proposalIndex, gradeIndex = judgment.grade)
            }
        }

        val result: Result = deliberation.deliberate(tally).toResult()

        val stylist = TextStylist()
        val groups: MutableList<DuelGroups> = mutableListOf()
        val explanations: MutableList<AnnotatedString> = mutableListOf()
        result.proposalResultsRanked.forEachIndexed { displayIndex, _ ->
            val otherIndex = if (displayIndex < amountOfProposals - 1) {
                displayIndex + 1
            } else {
                max(0, displayIndex - 1)
            }
            val duelAnalyzer = DuelAnalyzer(
                poll = filteredPoll,
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
                proportions[proportionalAlgorithm] = proportionalAlgorithm.compute(
                    filteredPoll,
                    result,
                )
            }
        }

        var lotterySeed: Long = 666010999
        if (poll.uuid != null) {
            lotterySeed = poll.uuid.mostSignificantBits
        }

        val lottery = mutableMapOf<ProportionalAlgorithms, List<Int>>()
        for (proportionalAlgorithm in ProportionalAlgorithms.entries) {
            if (proportionalAlgorithm.isAvailable()) {
                lottery[proportionalAlgorithm] = InnocentHand(seed = lotterySeed)
                    .pickWinners(weights = proportions[proportionalAlgorithm]!!)
            }
        }

        val proximityAnalysis = ProximityAnalyzer().analyze(
            poll = filteredPoll,
        )

        _viewState.update {
            it.copy(
                ballotFilter = ballotFilter,
                unfilteredPoll = poll,
                poll = filteredPoll,
                tally = tally.toTally(),
                result = result,
                explanations = explanations,
                groups = groups,
                proportions = proportions,
                lottery = lottery,
                proximityAnalysis = proximityAnalysis,
                highGradeOnLeft = sharedPrefsHelper.getHighGradeOnLeft(),
            )
        }
    }

    fun onFinish() {
        viewModelScope.launch {
            _navEvents.emit(NavigationAction.Clear)
        }
    }
}
