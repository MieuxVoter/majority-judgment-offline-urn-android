package com.illiouchine.jm.model.dto

import androidx.compose.runtime.Stable
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.math.min

@Stable
@Serializable
/**
 * This lightweight Data Transfer Object is what's encoded in the QR Code.
 */
data class BallotsDto(
    @Serializable(UUIDSerializer::class)
    val pollUuid: UUID,
    val ballots: List<Ballot>,
) {
    fun splitInto(amountOfPieces: Int): List<BallotsDto> {
        val amountOfBallots = this.ballots.size

        require(amountOfPieces > 0)
        require(amountOfPieces <= amountOfBallots)

        val rabiot = if (amountOfBallots % amountOfPieces == 0) { 0 } else { 1 }
        val share = amountOfBallots / amountOfPieces + rabiot // Euclid, mah man
        return buildList {
            for (i in 0..<amountOfPieces) {
                add(
                    copy(
                        ballots = ballots.subList(
                            i * share,
                            min(
                                amountOfBallots,
                                (i + 1) * share,
                            ),
                        ),
                    )
                )
            }
        }
    }
}