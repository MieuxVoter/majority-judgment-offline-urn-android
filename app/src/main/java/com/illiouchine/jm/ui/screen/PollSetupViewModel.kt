package com.illiouchine.jm.ui.screen

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Poll

class PollSetupViewModel : ViewModel() {
    data class PollSetupViewState(
        val poll: Poll,
    )
}