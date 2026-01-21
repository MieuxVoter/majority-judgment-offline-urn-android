package com.illiouchine.jm.model

import fr.mieuxvoter.mj.ProposalResultInterface

data class ProposalResult(
    val index: Int,
    val rank: Int,
    val relativeMerit: Double,
    val analysis: ProposalTallyAnalysis,
)

fun ProposalResultInterface.toProposalResult(): ProposalResult {
    return ProposalResult(
        index = this.index,
        rank = this.rank,
        relativeMerit = this.relativeMerit,
        analysis = this.analysis.toAnalysis()
    )
}
