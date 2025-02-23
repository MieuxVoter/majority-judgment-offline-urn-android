package com.illiouchine.jm.ui.screen

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.PollConfig

class PollSetupViewModel : ViewModel() {
    data class PollSetupViewState(
        val pollConfig: PollConfig,
    )
}