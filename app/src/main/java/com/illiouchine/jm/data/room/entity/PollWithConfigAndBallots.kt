package com.illiouchine.jm.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PollWithConfigAndBallots(
    @Embedded val poll: PollEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "pollId"
    )
    val pollConfigEntity: PollConfigEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "pollId"
    )
    val ballotEntity: BallotEntity,
)