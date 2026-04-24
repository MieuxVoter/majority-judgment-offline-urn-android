package com.illiouchine.jm.filters

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll

class NoBallotsFilter : BallotsFilterInterface {

    override fun shouldKeep(ballot: Ballot): Boolean {
        return true
    }

    override fun render(
        poll: Poll,
        onFilterDelete: () -> Unit,
        onFilterUpdate: (BallotsFilterInterface) -> Unit,
    ): @Composable (() -> Unit) {
        return (@Composable {
            // We do not render this filter right now.
            //Text("No filter is applied on the ballots.")
        })
    }
}

