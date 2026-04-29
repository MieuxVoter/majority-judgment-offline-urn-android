package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class Poll(
    val id: Int = 0,
    val pollConfig: PollConfig,
    val ballots: List<Ballot>,
) {

    val judgments: List<Judgment>
        get() = ballots.flatMap { ballot ->
            ballot.judgments
        }
}
