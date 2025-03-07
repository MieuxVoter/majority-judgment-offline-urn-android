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
        val playSound: Boolean = true,
        val pinScreen: Boolean = false,
        val defaultGrading: Grading = Grading.Quality7Grading,
    )

    private val _settingsViewState = MutableStateFlow<SettingsViewState>(SettingsViewState())
    val settingsViewState: StateFlow<SettingsViewState> = _settingsViewState

    init {
        loadShowOnboarding()
        loadPlaySound()
        loadPinScreen()
        loadDefaultGrading()
    }

    private fun loadShowOnboarding() {
        val showOnboarding = sharedPreferences.getShowOnboarding()
        _settingsViewState.update {
            it.copy(showOnboarding = showOnboarding)
        }
    }

    private fun loadPlaySound() {
        val playSound = sharedPreferences.getPlaySound()
        _settingsViewState.update {
            it.copy(playSound = playSound)
        }
    }

    private fun loadPinScreen() {
        val pinScreen = sharedPreferences.getPinScreen()
        _settingsViewState.update {
            it.copy(pinScreen = pinScreen)
        }
    }

    private fun loadDefaultGrading() {
        val defaultGrading = sharedPreferences.getDefaultGrading()
        _settingsViewState.update {
            it.copy(defaultGrading = defaultGrading)
        }
    }

    fun updateShowOnBoarding(visibility: Boolean) {
        sharedPreferences.editShowOnboarding(visibility)
        _settingsViewState.update {
            it.copy(showOnboarding = visibility)
        }
    }

    fun updatePlaySound(soundIsOn: Boolean) {
        sharedPreferences.editPlaySound(soundIsOn)
        _settingsViewState.update {
            it.copy(playSound = soundIsOn)
        }
    }

    fun updatePinScreen(pinScreen: Boolean) {
        sharedPreferences.editPinScreen(pinScreen)
        _settingsViewState.update {
            it.copy(pinScreen = pinScreen)
        }
    }

    fun updateDefaultGrading(grading: Grading) {
        sharedPreferences.editDefaultGrading(grading)
        _settingsViewState.update {
            it.copy(defaultGrading = grading)
        }
    }
}