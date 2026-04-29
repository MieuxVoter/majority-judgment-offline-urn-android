package com.illiouchine.jm.filters

import androidx.compose.runtime.Composable
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import kotlinx.serialization.Serializable

@Serializable
class NoBallotsFilter : BallotsFilterInterface {

    override fun shouldKeep(ballot: Ballot): Boolean {
        return true
    }

    override fun render(
        poll: Poll,
        onFilterDelete: () -> Unit,
        onFilterUpdate: (BallotsFilterInterface) -> Unit,
    ): @Composable (() -> Unit) {
        return (
            @Composable {
                // We do not render this filter right now.
                // Text("No filter is applied on the ballots.")
            }
            )
    }
}
