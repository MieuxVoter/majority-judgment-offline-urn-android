package com.illiouchine.jm.data

import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import java.util.UUID

interface PollDataSource {

    suspend fun saveBallot(ballot: Ballot, pollId: Int): Int

    suspend fun savePoll(poll: Poll): Int

    suspend fun getPollById(pollId: Int): Poll?

    suspend fun getPollByUuid(pollUuid: UUID): Poll?

    suspend fun getAllPolls(): List<Poll>

    suspend fun deletePoll(poll: Poll)
}
