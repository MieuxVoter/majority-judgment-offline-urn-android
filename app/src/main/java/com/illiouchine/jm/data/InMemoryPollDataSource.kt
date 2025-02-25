package com.illiouchine.jm.data

import com.illiouchine.jm.model.Poll


//Todo : Should not be accessed directly,
// 1 - setup a pollRepository
// 2 - in Repository : make the polls reactive by wrapping it in Flow
class InMemoryPollDataSource : PollDataSource {
    private val polls: MutableList<Poll> = mutableListOf()

    override suspend fun savePolls(poll: Poll) {
        polls.add(poll)
    }

    override suspend fun getAllPoll(): List<Poll> {
        return polls.toList()
    }

    override suspend fun deletePoll(poll: Poll) {
        polls.remove(poll)
    }
}