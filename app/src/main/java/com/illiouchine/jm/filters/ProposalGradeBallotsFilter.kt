package com.illiouchine.jm.filters

import android.os.Parcel
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import kotlinx.serialization.Serializable

@Serializable
class ProposalGradeBallotsFilter(
    val proposalIndex: Int,
    val gradeIndex: Int,
) : BallotsFilterInterface {

    override fun shouldKeep(ballot: Ballot): Boolean {
        return ballot.gradeOf(proposalIndex) == gradeIndex
    }

    override fun render(
        poll: Poll,
        onFilterDelete: () -> Unit,
        onFilterUpdate: (BallotsFilterInterface) -> Unit,
    ): @Composable (() -> Unit) {
        val filter = this
        return (@Composable {
            Row {
                FlowRow(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                ) {
                    val fontSizeTextButton = 14.sp
                    var proposalDropdownExpanded by remember { mutableStateOf(false) }
                    var gradeDropdownExpanded by remember { mutableStateOf(false) }

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = "judge",
                        fontSize = fontSizeTextButton,
                    )

                    TextButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .semantics {
                                onClick(
                                    label = "pick a proposal for this filter",
                                    action = null,
                                )
                            },
                        onClick = {
                            proposalDropdownExpanded = !proposalDropdownExpanded
                        },
                    ) {
                        Text(" ${poll.pollConfig.getProposalName(proposalIndex)} ")
                    }

                    DropdownMenu(
                        expanded = proposalDropdownExpanded,
                        onDismissRequest = {
                            proposalDropdownExpanded = false
                        },
                    ) {
                        for ((proposalIndex, proposal) in poll.pollConfig.proposals.withIndex()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .semantics {
                                        contentDescription = "Proposal"
                                    },
                                enabled = true,
                                text = {
                                    Text(proposal)
                                },
                                onClick = {
                                    proposalDropdownExpanded = false
                                    onFilterUpdate(
                                        // Idea: use a .copy() instead?
                                        ProposalGradeBallotsFilter(
                                            proposalIndex = proposalIndex,
                                            gradeIndex = filter.gradeIndex,
                                        )
                                    )
                                },
                            )
                        }
                    }

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = "to be",
                        fontSize = fontSizeTextButton,
                    )

                    TextButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .semantics {
                                onClick(
                                    label = "pick a grade for this filter",
                                    action = null,
                                )
                            },
                        onClick = {
                            gradeDropdownExpanded = !gradeDropdownExpanded
                        },
                    ) {
                        Text("${stringResource(poll.pollConfig.grading.getGradeName(gradeIndex))}.")
                    }

                    DropdownMenu(
                        expanded = gradeDropdownExpanded,
                        onDismissRequest = {
                            gradeDropdownExpanded = false
                        },
                    ) {
                        for ((gradeIndex, _) in poll.pollConfig.grading.grades.withIndex()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .semantics {
                                        contentDescription = "Grade"
                                    },
                                enabled = true,
                                text = {
                                    Text(stringResource(
                                        poll.pollConfig.grading.getGradeName(gradeIndex)
                                    ))
                                },
                                onClick = {
                                    gradeDropdownExpanded = false
                                    onFilterUpdate(
                                        // Idea: use a .copy() instead?
                                        ProposalGradeBallotsFilter(
                                            proposalIndex = filter.proposalIndex,
                                            gradeIndex = gradeIndex,
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                ) {
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
