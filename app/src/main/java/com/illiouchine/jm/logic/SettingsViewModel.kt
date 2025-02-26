package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Grading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val sharedPreferences: SharedPrefsHelper,
) : ViewModel() {
    data class SettingsViewState(
        val showOnboarding: Boolean = true,
        val defaultGrading: Grading = Grading.Quality7Grading
    )

    private val _settingsViewState = MutableStateFlow<SettingsViewState>(SettingsViewState())
    val settingsViewState: StateFlow<SettingsViewState> = _settingsViewState

    init {
        loadShowOnboarding()
        loadDefaultGrading()
    }

    private fun loadDefaultGrading() {
        val defaultGrading = sharedPreferences.getDefaultGrading()
        _settingsViewState.update {
            it.copy(defaultGrading = defaultGrading)
        }
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

    fun updateDefaultGrading(grading: Grading) {
        sharedPreferences.editDefaultGrading(grading)
        _settingsViewState.update {
            it.copy(defaultGrading = grading)
        }
    }
}