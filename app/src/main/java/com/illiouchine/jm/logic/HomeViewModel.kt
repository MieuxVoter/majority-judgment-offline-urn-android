package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.model.Poll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val pollDataSource: InMemoryPollDataSource
) : ViewModel() {

    data class HomeViewState(
        val polls: List<Poll> = emptyList()
    )

    private val _homeViewState = MutableStateFlow<HomeViewState>(HomeViewState())
    val homeViewState: StateFlow<HomeViewState> = _homeViewState

    init {
        loadPolls()
    }

    private fun loadPolls() {
        _homeViewState.update {
            it.copy(polls = pollDataSource.getAllPoll())
        }
    }

    fun savePolls(poll: Poll) {
        pollDataSource.savePolls(poll)
        loadPolls()
    }

    fun deletePoll(poll: Poll) {
        pollDataSource.deletePoll(poll)
        loadPolls()
    }
}

