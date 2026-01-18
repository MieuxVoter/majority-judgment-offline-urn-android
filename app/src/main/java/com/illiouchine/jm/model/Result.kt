package com.illiouchine.jm.model

import fr.mieuxvoter.mj.ResultInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class Result(
    val proposalResults: ImmutableList<ProposalResult>,
    val proposalResultsRanked: ImmutableList<ProposalResult>,
)

fun ResultInterface.toResult(): Result {
    return Result(
        proposalResults = this.proposalResults
            .map { it.toProposalResult() }
            .toImmutableList(),
        proposalResultsRanked = this.proposalResultsRanked
            .map { it.toProposalResult() }
            .toImmutableList()
    )
}
