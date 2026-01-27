package com.illiouchine.jm.model

import fr.mieuxvoter.mj.TallyInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class Tally(
    val proposalsTallies: ImmutableList<ProposalTally>
)

fun TallyInterface.toTally(): Tally {
    return Tally(
        proposalsTallies = this.proposalsTallies.map { tallyInterface ->
            tallyInterface.toProposalTally()
        }.toImmutableList()
    )
}
