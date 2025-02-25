package com.illiouchine.jm.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PollWithProposals(
    @Embedded val poll: PollEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "pollId"
    )
    val proposals: List<ProposalEntity>
)