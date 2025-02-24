package com.illiouchine.jm.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PollEntity(
    @PrimaryKey val uid: Int,
)

@Entity
data class PollConfigEntity(
    @PrimaryKey val uid: Int,
    val poolId: Int,
    val nbGrading: Int,
)
@Entity
data class ProposalsEntity(
    @PrimaryKey val uid: Int,
    val name: String
)

@Entity
data class BallotEntity(
    @PrimaryKey val uid: Int,
    val poolId: Int,
)

@Entity
data class JudgmentEntity(
    @PrimaryKey val uid: Int,
    val ballotId: Int,
    val proposalId: Int,
    val grade : Int
)
