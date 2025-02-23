package com.illiouchine.jm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val sharedPreferences: SharedPrefsHelper,
) : ViewModel() {
    data class SettingsViewState(
        val showOnboarding: Boolean = true,
        //val defaultListGrading: ListGrading = Quality7Grading()
    )

    private val _settingsViewState = MutableStateFlow<SettingsViewState>(SettingsViewState())
    val settingsViewState: StateFlow<SettingsViewState> = _settingsViewState

    init {
        loadShowOnboarding()
    }

    private fun loadShowOnboarding() {
        val showOnboarding = sharedPreferences.getShowOnboarding()
        _settingsViewState.update {
            it.copy(showOnboarding = showOnboarding)
        }
    }

    fun updateShowOnBoarding(visibility: Boolean) {
        sharedPreferences.editShowOnboarding(visibility)
        _settingsViewState.update {
            it.copy(showOnboarding = visibility)
        }
    }
}