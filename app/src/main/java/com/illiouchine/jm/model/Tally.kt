package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import fr.mieuxvoter.kmj.tally.PollTallyInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Stable
data class Tally(
    val proposalsTallies: ImmutableList<ProposalTally>,
)

fun PollTallyInterface.toTally(): Tally {
    return Tally(
        proposalsTallies = this.candidatesTallies.map { candidateTally ->
            candidateTally.toProposalTally()
        }.toPersistentList(),
    )
}
