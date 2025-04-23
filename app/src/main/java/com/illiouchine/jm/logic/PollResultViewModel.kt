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
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ResultInterface
import fr.mieuxvoter.mj.TallyInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PollResultViewModel(
    private val navigator: Navigator,
    private val pollDataSource: PollDataSource,
) : ViewModel() {

    data class PollResultViewState(
        val poll: Poll? = null,
        val tally: TallyInterface? = null,
        val result: ResultInterface? = null,
        val explanations: List<AnnotatedString> = emptyList(),
        val groups: List<DuelGroups> = emptyList(),
    )

    data class DuelGroups(
        val groups: List<ParticipantGroupAnalysis>,
    )

    private val _pollResultViewState = MutableStateFlow(PollResultViewState())
    val pollResultViewState: StateFlow<PollResultViewState> = _pollResultViewState

    fun initializePollResultById(context: Context, pollId: Int) {
        viewModelScope.launch {
            val poll = pollDataSource.getPollById(pollId)

            if (poll == null) {
                Toast.makeText(context,
                    context.getString(R.string.toast_that_poll_does_not_exist), Toast.LENGTH_LONG).show()
                navigator.navigateTo(destination = Screens.Home)
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

        _pollResultViewState.update {
            it.copy(
                poll = poll,
                tally = tally,
                result = result,
                explanations = explanations,
                groups = groups,
            )
        }
    }

    fun onFinish() {
        viewModelScope.launch {
            navigator.navigateTo(Screens.Home)
        }
    }
}
