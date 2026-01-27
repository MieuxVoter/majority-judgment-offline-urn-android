package com.illiouchine.jm.data

import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll

// This is now basically only a dummy used in @Previews, ever since we've had SqlitePollDataSource.
// Todo : Should not be accessed directly,
// 1 - setup a pollRepository
// 2 - in Repository : make the polls reactive by wrapping it in Flow
class InMemoryPollDataSource : PollDataSource {
    private val polls: MutableList<Poll> = mutableListOf()

    override suspend fun saveBallot(ballot: Ballot, pollId: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun savePoll(poll: Poll): Int {
        polls.add(poll)
        return polls.size - 1
    }

    override suspend fun getPollById(pollId: Int): Poll? {
        polls.forEach {
            if (it.id == pollId) {
                return it
            }
        }
        return null
    }

    override suspend fun getAllPolls(): List<Poll> {
        return polls.toList()
    }

    override suspend fun deletePoll(poll: Poll) {
        polls.remove(poll)
    }
}
