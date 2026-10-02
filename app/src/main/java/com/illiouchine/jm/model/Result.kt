package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import fr.mieuxvoter.kmj.result.PollResultInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Stable
data class Result(
    val proposalResults: ImmutableList<ProposalResult>,
    val proposalResultsRanked: ImmutableList<ProposalResult>,
)

fun PollResultInterface.toResult(): Result {
    return Result(
        proposalResults = this.candidateResults
            .map { it.toProposalResult() }
            .toPersistentList(),
        proposalResultsRanked = this.candidateResultsRanked
            .map { it.toProposalResult() }
            .toPersistentList()
    )
}
