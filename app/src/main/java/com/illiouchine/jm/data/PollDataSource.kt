package com.illiouchine.jm.data

import com.illiouchine.jm.model.Poll

interface PollDataSource {

    fun savePolls(poll: Poll)

    fun getAllPoll(): List<Poll>

    fun deletePoll(poll: Poll)
}