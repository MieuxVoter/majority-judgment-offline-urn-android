package com.illiouchine.jm.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PollConfigWithProposals(
    @Embedded val pollConfig: PollConfigEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "pollConfigId"
    )
    val proposals: List<ProposalEntity>
)