package com.illiouchine.jm.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.illiouchine.jm.data.room.entity.BallotEntity
import com.illiouchine.jm.data.room.entity.BallotWithJudgment
import com.illiouchine.jm.data.room.entity.JudgmentEntity
import com.illiouchine.jm.data.room.entity.PollEntity
import com.illiouchine.jm.data.room.entity.PollWithProposals
import com.illiouchine.jm.data.room.entity.ProposalEntity

/**
 * Data Access Object
 */
@Dao
interface PollDao {

    @Transaction
    @Query("SELECT * FROM poll")
    suspend fun loadPolls(): List<PollWithProposals>

    @Transaction
    @Query("SELECT * FROM poll WHERE uid = :id LIMIT 1")
    suspend fun loadPoll(id: Int): PollWithProposals

    @Transaction
    @Query("SELECT * FROM ballot WHERE pollId = :pollId")
    suspend fun loadBallots(pollId: Int): List<BallotWithJudgment>

    @Transaction
    suspend fun insertPoll(
        poll: PollEntity,
        proposals: List<ProposalEntity>,
        ballots: List<List<JudgmentEntity>>,
    ): Int {
        val pollId = insertPollDatumOnly(poll).toInt()
        insertProposals(
            proposals.map { proposal ->
                proposal.copy(pollId = pollId)
            }
        )
        ballots.forEach { judgments ->
            val ballotId = insertBallot(
                BallotEntity(pollId = pollId)
            ).toInt()
            insertJudgment(
                judgments.map { judgment ->
                    judgment.copy(ballotId = ballotId)
                }
            )
        }

        return pollId
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPollDatumOnly(poll: PollEntity): Long

    @Insert
    suspend fun insertProposals(proposals: List<ProposalEntity>): List<Long>

    @Insert
    suspend fun insertBallot(ballot: BallotEntity): Long

    @Insert
    suspend fun insertJudgment(judgments: List<JudgmentEntity>): List<Long>

    @Delete(entity = PollEntity::class)
    suspend fun deletePoll(poll: PollEntity)
}
