package com.illiouchine.jm.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.illiouchine.jm.data.room.entity.BallotEntity
import com.illiouchine.jm.data.room.entity.JudgmentEntity
import com.illiouchine.jm.data.room.entity.PollConfigEntity
import com.illiouchine.jm.data.room.entity.PollEntity
import com.illiouchine.jm.data.room.entity.PollWithConfigAndBallots
import com.illiouchine.jm.data.room.entity.ProposalEntity

@Dao
interface PollDao {
/*
    @Transaction
    @Query("SELECT * FROM poll")
    fun loadPolls(): List<PollWithConfigAndBallots>

    @Transaction
    fun insertPollFull(
        pollFull: PollWithConfigAndBallots
    ){
        val pollId = insertPoll(pollFull.poll)
        val configId = insertConfig(
            pollFull.pollConfigEntity.copy(pollId = pollId)
        )
        insertProposals(
            pollFull.pollConfigWithProposals.proposals.map { proposal ->
                proposal.copy(pollConfigId = configId)
            }
        )
        pollFull.ballotsWithJudgment.forEach { ballotWithJudgment ->
            val ballotId = insertBallot(
                ballotWithJudgment.ballot.copy(pollId = pollId)
            )
            insertJudgment(
                ballotWithJudgment.judgments.map { judgment ->
                    judgment.copy(ballotId = ballotId)
                }
            )

        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoll(poll: PollEntity) : Int

    @Insert
    fun insertConfig(config: PollConfigEntity): Int

    @Insert
    fun insertProposals(proposals: List<ProposalEntity>): Int

    @Insert
    fun insertBallot(ballot: BallotEntity) : Int

    @Insert
    fun insertJudgment(judgments: List<JudgmentEntity>): Int

 */
}