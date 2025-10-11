package com.illiouchine.jm.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.PollTemplateDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollTemplate
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val pollDataSource: PollDataSource,
    private val pollTemplateDataSource: PollTemplateDataSource,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val navigator: Navigator,
    application: Application,
) : AndroidViewModel(application) {

    data class HomeViewState(
        val polls: List<Poll> = emptyList(),
        val templates: List<PollTemplate> = emptyList(),
    )

    private val _homeViewState = MutableStateFlow(HomeViewState())
    val homeViewState: StateFlow<HomeViewState> = _homeViewState

    fun initialize() {
        loadPolls()
        loadPollTemplates()
        loadDefaultSettings()
    }

    private fun loadDefaultSettings() {
        viewModelScope.launch {
            val showOnboarding = sharedPrefsHelper.getShowOnboarding()
            if (showOnboarding){
                navigator.navigateTo(
                    Screens.OnBoarding,
                    navOptions = {
                        launchSingleTop = true
                    }
                )
            }
        }
    }

    private fun loadPolls() {
        viewModelScope.launch {
            val polls = pollDataSource.getAllPolls()
            _homeViewState.update {
                it.copy(polls = polls)
            }
        }
    }

    private fun loadPollTemplates() {
        viewModelScope.launch {
            val slugs = pollTemplateDataSource.getAvailableSlugs()
            val templates = List(
                size = slugs.size,
                init = {
                    PollTemplate(
                        slug = slugs[it],
                        config = pollTemplateDataSource.getBySlug(
                            slugs[it],
                            getApplication<Application>().applicationContext,
                        ),
                    )
                },
            )
            _homeViewState.update {
                it.copy(templates = templates)
            }
        }
    }

    fun setupBlankPoll() {
        viewModelScope.launch {
            navigator.navigateTo(Screens.PollSetup(cloneablePollId = 0))
        }
    }

    fun setupPollFromTemplate(pollTemplateSlug: String) {
        viewModelScope.launch {
            navigator.navigateTo(Screens.PollSetup(
                cloneablePollId = 0,
                pollTemplateSlug = pollTemplateSlug,
            ))
        }
    }

    fun deletePoll(poll: Poll) {
        viewModelScope.launch {
            pollDataSource.deletePoll(poll)
            loadPolls()
        }
    }

    fun clonePoll(poll: Poll) {
        viewModelScope.launch {
            navigator.navigateTo(Screens.PollSetup(cloneablePollId = poll.id))
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
}
