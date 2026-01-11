package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.ui.navigator.NavigationAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    private val prefsHelper: SharedPrefsHelper,
) : ViewModel() {

    private val _navEvents = MutableSharedFlow<NavigationAction>()
    val navEvents = _navEvents.asSharedFlow()

    fun finish(){
        viewModelScope.launch {
            prefsHelper.editShowOnboarding(false)
            _navEvents.emit(NavigationAction.Back)
        }
    }

}
