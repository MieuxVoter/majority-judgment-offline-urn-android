package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.illiouchine.jm.R
import com.illiouchine.jm.filters.BallotsFilterInterface
import com.illiouchine.jm.filters.NoBallotsFilter
import com.illiouchine.jm.model.Ballot
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BallotCountRow(
    modifier: Modifier = Modifier,
    ballots: ImmutableList<Ballot> = persistentListOf(),
    unfilteredBallots: ImmutableList<Ballot> = persistentListOf(),
    ballotsFilter: BallotsFilterInterface = NoBallotsFilter(),
) {
    val amountOfBallots = ballots.size
    Row(
        modifier = modifier,
    ) {
        // Another pluralization that should use R.plurals instead
        val ballotsString = if (amountOfBallots <= 1) {
            stringResource(R.string.ballot)
        } else {
            stringResource(R.string.ballots)
        }

        val text = if (ballotsFilter is NoBallotsFilter) {
            "$amountOfBallots $ballotsString " + stringResource(R.string.in_the_urn)
        } else {
            val totalAmountOfBallots = unfilteredBallots.size
            stringResource(R.string.using) + " $amountOfBallots / $totalAmountOfBallots $ballotsString"
        }

        Text(
            text = text,
        )
    }
}
