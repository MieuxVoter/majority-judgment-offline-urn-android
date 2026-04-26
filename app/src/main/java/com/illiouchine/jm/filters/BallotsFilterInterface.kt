package com.illiouchine.jm.filters

import androidx.compose.runtime.Composable
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll

interface BallotsFilterInterface {

    fun shouldKeep(ballot: Ballot): Boolean

    fun render(
        poll: Poll,
        onFilterDelete: () -> Unit = {},
        onFilterUpdate: (BallotsFilterInterface) -> Unit = {},
    ): @Composable () -> Unit

}
