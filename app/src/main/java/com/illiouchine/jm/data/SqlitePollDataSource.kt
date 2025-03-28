package com.illiouchine.jm.data

import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.data.room.entity.BallotEntity
import com.illiouchine.jm.data.room.toDomainObject
import com.illiouchine.jm.data.room.toListOfJudgments
import com.illiouchine.jm.data.room.toPollEntity
import com.illiouchine.jm.data.room.toProposalsEntity
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig

/**
 * Sqlite database, handled by Room.
 */
class SqlitePollDataSource(
    private val pollDao: PollDao,
) : PollDataSource {

    override suspend fun saveBallot(ballot: Ballot, pollId: Int): Int {
        val ballotId = pollDao.insertBallot(
            BallotEntity(pollId = pollId)
        ).toInt()
        pollDao.insertJudgment(
            ballot.toListOfJudgments(ballotId)
        )
        return ballotId
    }

    override suspend fun savePoll(poll: Poll): Int {
        return pollDao.insertPoll(
            poll = poll.toPollEntity(),
            proposals = poll.toProposalsEntity(),
            ballots = poll.ballots.toListOfJudgments(),
        )
    }

    override suspend fun getPollById(pollId: Int): Poll? {
        try {
            val pnp = pollDao
                .loadPoll(pollId)

            val ballots = pollDao
                .loadBallots(pollId = pollId)
                .toDomainObject()

            val poll = Poll(
                id = pollId,
                pollConfig = PollConfig(
                    subject = pnp.poll.subject,
                    proposals = pnp.proposals.map { e -> e.name },
                    grading = Grading.byAmountOfGrades(amount = pnp.poll.nbGrading)
                ),
                ballots = ballots,
            )

            return poll

        } catch (_: NullPointerException) {
            return null
        }
    }

    override suspend fun getAllPolls(): List<Poll> {
        val loadBallot : suspend (pollId : Int) -> List<Ballot> = { pollId ->
            pollDao
                .loadBallots(pollId = pollId)
                .toDomainObject()
        }

        return pollDao
            .loadPolls()
            .toDomainObject { pollId ->
                loadBallot(pollId)
            }
    }

    override suspend fun deletePoll(poll: Poll) {
        val pollEntity = pollDao.loadPoll(poll.id)
        pollDao.deletePoll(pollEntity.poll)
    }
}