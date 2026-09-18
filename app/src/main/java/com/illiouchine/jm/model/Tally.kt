package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import fr.mieuxvoter.mj.TallyInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Stable
data class Tally(
    val proposalsTallies: ImmutableList<ProposalTally>
)

fun TallyInterface.toTally(): Tally {
    return Tally(
        proposalsTallies = this.proposalsTallies.map { tallyInterface ->
            tallyInterface.toProposalTally()
        }.toPersistentList()
    )
}
