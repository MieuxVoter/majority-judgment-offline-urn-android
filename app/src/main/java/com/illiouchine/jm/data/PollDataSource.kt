package com.illiouchine.jm.data

import com.illiouchine.jm.model.Poll

interface PollDataSource {

    suspend fun savePolls(poll: Poll)

    suspend fun getAllPoll(): List<Poll>

    suspend fun deletePoll(poll: Poll)
}