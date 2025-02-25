package com.illiouchine.jm.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BallotWithJudgment(
    @Embedded val ballot: BallotEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "ballotId"
    )
    val judgments: List<JudgmentEntity>
)