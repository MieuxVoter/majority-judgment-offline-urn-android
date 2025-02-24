package com.illiouchine.jm.data

import com.illiouchine.jm.model.Poll


//Todo : Should not be accessed directly,
// 1 - setup a pollRepository
// 2 - in Repository : make the polls reactive by wrapping it in Flow
class InMemoryPollDataSource : PollDataSource {
    private val polls: MutableList<Poll> = mutableListOf()

    fun savePolls(poll: Poll) {
        polls.add(poll)
    }

    fun getAllPoll(): List<Poll> {
        return polls.toList()
    }

    fun deletePoll(poll: Poll) {
        polls.remove(poll)
    }
}