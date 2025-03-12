package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val pollDataSource: PollDataSource,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val navigator: Navigator,
) : ViewModel() {

    data class HomeViewState(
        val polls: List<Poll> = emptyList(),
        val showOnboarding: Boolean = false,
    )

    private val _homeViewState = MutableStateFlow<HomeViewState>(HomeViewState())
    val homeViewState: StateFlow<HomeViewState> = _homeViewState

    init {
        loadPolls()
        loadDefaultSettings()
    }

    private fun loadDefaultSettings() {
        viewModelScope.launch {
            val showOnboarding = sharedPrefsHelper.getShowOnboarding()
            _homeViewState.update {
                it.copy(showOnboarding = showOnboarding)
            }
        }
    }

    fun loadPolls() {
        viewModelScope.launch {
            val polls = pollDataSource.getAllPolls()
            _homeViewState.update {
                it.copy(polls = polls)
            }
        }
    }

    fun savePoll(poll: Poll) {
        viewModelScope.launch {
            pollDataSource.savePoll(poll)
            loadPolls()
        }
    }

    fun deletePoll(poll: Poll) {
        viewModelScope.launch {
            pollDataSource.deletePoll(poll)
            loadPolls()
        }
    }

    fun setupBlankPoll() {
        viewModelScope.launch {
            navigator.navigateTo(Screens.PollSetup(0))
        }
    }

    fun clonePoll(poll: Poll) {
        viewModelScope.launch {
            navigator.navigateTo(Screens.PollSetup(id = poll.id))
        }
    }

    fun resumePoll(poll: Poll) {
        viewModelScope.launch {
            navigator.navigateTo(Screens.PollVote(id = poll.id))
        }
    }

    fun showResult(poll: Poll) {
        viewModelScope.launch {
            navigator.navigateTo(Screens.PollResult(id = poll.id))
        }
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            sharedPrefsHelper.editShowOnboarding(false)
            // hotfix: perhaps we should observe the prefs instead ?
            _homeViewState.update {
                it.copy(
                    showOnboarding = false,
                )
            }
        }
    }
}

