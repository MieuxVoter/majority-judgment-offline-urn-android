package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val pollDataSource: PollDataSource,
    private val navigator: Navigator,
    private val prefsHelper: SharedPrefsHelper,
) : ViewModel() {

    data class HomeViewState(
        val polls: List<Poll> = emptyList(),
        val showOnboarding: Boolean = false,
    )

    private val _homeViewState = MutableStateFlow<HomeViewState>(HomeViewState())
    val homeViewState: StateFlow<HomeViewState> = _homeViewState

    init {
        loadPolls()
        loadSharedPref()
    }

    private fun loadSharedPref() {
        viewModelScope.launch {
            val showOnboarding = prefsHelper.getShowOnboarding()
            _homeViewState.update {
                it.copy(showOnboarding = showOnboarding)
            }
        }
    }

    fun onOnboardingFinished() {
        viewModelScope.launch {
            prefsHelper.editShowOnboarding(false)
            _homeViewState.update {
                it.copy(showOnboarding = false)
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
}

