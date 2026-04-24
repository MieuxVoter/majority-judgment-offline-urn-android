package com.illiouchine.jm.filters

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll

class ProposalGradeBallotsFilter(
    val proposalIndex: Int,
    val gradeIndex: Int,
) : BallotsFilterInterface {

//    override fun filter(poll: Poll): Poll {
//        return poll.copy(
//            ballots = poll.ballots.filter {
//                shouldKeep(it)
//            },
//        )
//    }

    override fun shouldKeep(ballot: Ballot): Boolean {
        return ballot.gradeOf(proposalIndex) == gradeIndex
    }

    override fun render(
        poll: Poll,
        onFilterDelete: () -> Unit,
        onFilterUpdate: (BallotsFilterInterface) -> Unit,
    ): @Composable (() -> Unit) {
        return (@Composable {
            //Text("A filter is applied on the ballots!")
            Row {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                ) {
                    Text("Judge ${poll.pollConfig.getProposalName(proposalIndex)}")
                    Text(" as ")
                    Text("${stringResource(poll.pollConfig.grading.getGradeName(gradeIndex))}.")
                }
                Row {
                    IconButton(
                        onClick = {
                            onFilterDelete()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete this filter.",
                        )
                    }
                }
            }
        })
    }

}
