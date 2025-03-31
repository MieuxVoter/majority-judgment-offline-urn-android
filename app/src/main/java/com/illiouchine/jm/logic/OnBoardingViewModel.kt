package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.ui.Navigator
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    private val prefsHelper: SharedPrefsHelper,
    private val navigator: Navigator
) : ViewModel() {

    fun finish(){
        viewModelScope.launch {
            prefsHelper.editShowOnboarding(false)
            navigator.navigateUp()
        }
    }

}
