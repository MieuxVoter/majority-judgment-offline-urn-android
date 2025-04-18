package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sharedPreferences: SharedPrefsHelper,
    private val navigator: Navigator,
) : ViewModel() {

    data class SettingsViewState(
        val playSound: Boolean = DEFAULT_PLAY_SOUND_VALUE,
        val pinScreen: Boolean = DEFAULT_PIN_SCREEN_VALUE,
        val defaultGrading: Grading = DEFAULT_GRADING_QUALITY_VALUE,
    )

    private val _settingsViewState = MutableStateFlow(SettingsViewState())
    val settingsViewState: StateFlow<SettingsViewState> = _settingsViewState

    fun initialize() {
        loadPlaySound()
        loadPinScreen()
        loadDefaultGrading()
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

    fun showOnBoarding() {
        viewModelScope.launch {
            navigator.navigateTo(
                Screens.OnBoarding,
                navOptions = {
                    launchSingleTop = true
                }
            )
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
