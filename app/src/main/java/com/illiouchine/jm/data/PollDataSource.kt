package com.illiouchine.jm.data

import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll

interface PollDataSource {

    suspend fun saveBallot(ballot: Ballot, pollId: Int): Int

    suspend fun savePoll(poll: Poll): Int

    suspend fun getPollById(pollId: Int): Poll?

    suspend fun getAllPolls(): List<Poll>

    suspend fun deletePoll(poll: Poll)
}