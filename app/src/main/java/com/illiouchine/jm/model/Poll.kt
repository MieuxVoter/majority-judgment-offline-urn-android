package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import com.illiouchine.jm.model.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Stable
@Serializable
data class Poll(
    val id: Int = 0,
    @Serializable(UUIDSerializer::class)
    val uuid: UUID? = null,
    val pollConfig: PollConfig,
    val ballots: List<Ballot> = emptyList(),
) {

    val judgments: List<Judgment>
        get() = ballots.flatMap { ballot ->
            ballot.judgments
        }

    fun isBallotValid(ballot: Ballot): Boolean {
        for (judgment in ballot.judgments) {
            if (judgment.grade < 0) {
                return false
            }
            if (judgment.grade >= pollConfig.grading.grades.size) {
                return false
            }
            if (judgment.proposal < 0) {
                return false
            }
            if (judgment.proposal >= pollConfig.proposals.size) {
                return false
            }
        }

        return true
    }

}
