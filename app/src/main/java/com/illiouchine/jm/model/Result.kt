package com.illiouchine.jm.model

import fr.mieuxvoter.mj.ResultInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

data class Result(
    val proposalResults: ImmutableList<ProposalResult>,
    val proposalResultsRanked: ImmutableList<ProposalResult>,
)

fun ResultInterface.toResult(): Result {
    return Result(
        proposalResults = this.proposalResults
            .map { it.toProposalResult() }
            .toPersistentList(),
        proposalResultsRanked = this.proposalResultsRanked
            .map { it.toProposalResult() }
            .toPersistentList()
    )
}
