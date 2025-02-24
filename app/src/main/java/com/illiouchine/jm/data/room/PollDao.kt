package com.illiouchine.jm.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PollDao {

    @Transaction
    @Query("SELECT * FROM poll")
    fun loadPolls(): List<PoolWithConfigAndBallots>

    fun insertPoll(
        poll: PollEntity,
        config: PollConfigEntity,
        proposals: List<ProposalEntity>,
        ballots: List<BallotEntity>,
        judgments: List<JudgmentEntity>
    ){
        insertPoll(poll)
        insertConfig(config)
        insertProposals(proposals)
        insertBallots(ballots)
        insertJudgment(judgments)
    }

    @Insert
    fun insertPoll(poll: PollEntity)

    @Insert
    fun insertConfig(config: PollConfigEntity)

    @Insert
    fun insertProposals(proposals: List<ProposalEntity>)

    @Insert
    fun insertBallots(ballots: List<BallotEntity>)

    @Insert
    fun insertJudgment(judgments: List<JudgmentEntity>)
}