package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val pollDataSource: PollDataSource,
    private val navigator: Navigator,
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
        viewModelScope.launch {
            val polls = pollDataSource.getAllPoll()
            _homeViewState.update {
                it.copy(polls = polls)
            }
        }
    }

    fun savePolls(poll: Poll) {
        viewModelScope.launch {
            pollDataSource.savePolls(poll)
            loadPolls()
        }
    }

    fun deletePoll(poll: Poll) {
        viewModelScope.launch {
            pollDataSource.deletePoll(poll)
            loadPolls()
        }
    }
}

