package com.illiouchine.jm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val sharedPreferences: SharedPrefsHelper
): ViewModel() {

    data class MainViewState(
        val showOnboarding: Boolean = true,
    )

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState())
    val viewState: StateFlow<MainViewState> = _viewState

    init {
        loadShowOnboarding()
    }

    private fun loadShowOnboarding() {
        val showOnboarding = sharedPreferences.getShowOnboarding()
        _viewState.update {
            it.copy(showOnboarding = showOnboarding)
        }
    }

    fun onFinishOnBoarding(){
        sharedPreferences.editShowOnboarding(false)
        _viewState.update {
            it.copy(showOnboarding = false)
        }
    }


}

