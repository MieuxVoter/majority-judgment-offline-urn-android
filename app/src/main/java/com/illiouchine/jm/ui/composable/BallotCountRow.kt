package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Ballot

@Composable
fun BallotCountRow(
    modifier: Modifier = Modifier,
    ballots: List<Ballot> = emptyList(),
) {
    val amountOfBallots = ballots.size
    Row(
        modifier = modifier,
    ) {
        // Another pluralization that should use R.plurals instead
        val ballotsString = if (amountOfBallots <= 1)
            stringResource(R.string.ballot)
        else
            stringResource(R.string.ballots)

        Text(
            "${amountOfBallots} ${ballotsString} " + stringResource(R.string.in_the_urn)
        )
    }
}
