package com.illiouchine.jm.data

import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.data.room.toDomainObject
import com.illiouchine.jm.data.room.toListOfJudgments
import com.illiouchine.jm.data.room.toPollEntity
import com.illiouchine.jm.data.room.toProposalsEntity
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll

class BDDPollDataSource(
    private val pollDao: PollDao
) : PollDataSource {

    override suspend fun savePolls(poll: Poll) {
        pollDao.insertPoll(
            poll = poll.toPollEntity(),
            proposals = poll.toProposalsEntity(),
            ballots = poll.ballots.toListOfJudgments()
        )
    }

    override suspend fun getAllPoll(): List<Poll> {
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