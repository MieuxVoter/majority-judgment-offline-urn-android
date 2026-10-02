package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import fr.mieuxvoter.kmj.result.CandidateResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Stable
data class ProposalResult(
    val index: Int,
    val rank: Int,
    val relativeMerit: Double,
    val analysis: ProposalTallyAnalysis,
    val decisiveGroups: ImmutableList<ParticipantGroup>,
)

fun CandidateResult.toProposalResult(): ProposalResult {
    return ProposalResult(
        index = this.index,
        rank = this.rank,
        relativeMerit = this.relativeMerit,
        analysis = this.analysis.toAnalysis(),
        decisiveGroups = this.analysis.collectDecisiveGroups()
            .map { it.toParticipantGroup() }
            .toPersistentList(),
    )
}
