package com.illiouchine.jm.data.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity("poll")
data class PollEntity(
    @PrimaryKey val uid: Int,
)

@Entity("poll_config")
data class PollConfigEntity(
    @PrimaryKey val uid: Int,
    val poolId: Int,
    val nbGrading: Int,
)
@Entity("proposal")
data class ProposalEntity(
    @PrimaryKey val uid: Int,
    val pollConfigId: Int,
    val name: String
)

@Entity("ballot")
data class BallotEntity(
    @PrimaryKey val uid: Int,
    val poolId: Int,
)

@Entity("Judgment")
data class JudgmentEntity(
    @PrimaryKey val uid: Int,
    val ballotId: Int,
    val proposalIndex: Int,
    val gradeIndex : Int
)

data class PoolWithConfigAndBallots(
    @Embedded val poll: PollEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "pollId"
    )
    val pollConfig: PollConfigEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "pollId"
    )
    val ballots: List<BallotWithJudgment>,
)

data class PollConfigWithProposals(
    @Embedded val pollConfig: PollConfigEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "pollConfigId"
    )
    val proposals: List<ProposalEntity>
)

data class BallotWithJudgment(
    @Embedded val ballot: BallotEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "ballotId"
    )
    val judgments: List<JudgmentEntity>
)
