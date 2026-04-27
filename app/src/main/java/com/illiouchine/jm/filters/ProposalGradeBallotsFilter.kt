package com.illiouchine.jm.filters

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.serialization.Serializable

@Serializable
sealed class GradeComparator(
    @get:StringRes val word: Int,
    val compare: (ballotGrade: Int, grade: Int) -> Boolean,
) {

    class ExactGradeComparator: GradeComparator(
        word = R.string.grade_comparator_exactly,
        compare = { ballotGrade, grade ->
            ballotGrade == grade
        },
    )

    class AtLeastGradeComparator: GradeComparator(
        word = R.string.grade_comparator_at_least,
        compare = { ballotGrade, grade ->
            ballotGrade >= grade
        },
    )

    class AtMostGradeComparator: GradeComparator(
        word = R.string.grade_comparator_at_most,
        compare = { ballotGrade, grade ->
            ballotGrade <= grade
        },
    )

}

@Serializable
class ProposalGradeBallotsFilter(
    val proposalIndex: Int,
    val gradeIndex: Int,
    val comparator: GradeComparator,
) : BallotsFilterInterface {

    override fun shouldKeep(ballot: Ballot): Boolean {
//        return ballot.gradeOf(proposalIndex) == gradeIndex
        return comparator.compare(ballot.gradeOf(proposalIndex), gradeIndex)
    }

    override fun render(
        poll: Poll,
        onFilterDelete: () -> Unit,
        onFilterUpdate: (BallotsFilterInterface) -> Unit,
    ): @Composable (() -> Unit) {

        val filter = this
        return (@Composable {
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Theme.colorScheme.primary,
                    )
                    .padding(
                        start = Theme.spacing.small,
                    )
            ) {
                FlowRow(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
//                    verticalArrangement = spacedBy(0.dp, Alignment.Top),
                ) {
                    val fontSizeTextButton = 14.sp
                    var proposalDropdownExpanded by remember { mutableStateOf(false) }
                    var gradeDropdownExpanded by remember { mutableStateOf(false) }
                    var comparatorDropdownExpanded by remember { mutableStateOf(false) }

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = "judge",
                        fontSize = fontSizeTextButton,
                    )

                    val buttonPadding = PaddingValues(
                        start = 3.dp,
                        top = 0.dp,
                        end = 3.dp,
                        bottom = 0.dp,
                    )
                    val buttonMinHeight = 28.dp
                    val buttonMinWidth = 28.dp

                    TextButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .heightIn(min = buttonMinHeight)
                            .widthIn(min = buttonMinWidth)
                            .semantics {
                                onClick(
                                    label = "pick a proposal for this filter",
                                    action = null,
                                )
                            },
                        shape = MaterialTheme.shapes.small,
                        contentPadding = buttonPadding,
                        onClick = {
                            proposalDropdownExpanded = !proposalDropdownExpanded
                        },
                    ) {
                        Text(poll.pollConfig.getProposalName(proposalIndex))
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
                                            comparator = filter.comparator,
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
                            .heightIn(min = buttonMinHeight)
                            .widthIn(min = buttonMinWidth)
                            .semantics {
                                onClick(
                                    label = "pick a comparator for this filter",
                                    action = null,
                                )
                            },
                        shape = MaterialTheme.shapes.small,
                        contentPadding = buttonPadding,
                        onClick = {
                            comparatorDropdownExpanded = !comparatorDropdownExpanded
                        },
                    ) {
                        Text(stringResource(filter.comparator.word))
                    }
                    DropdownMenu(
                        expanded = comparatorDropdownExpanded,
                        onDismissRequest = {
                            comparatorDropdownExpanded = false
                        },
                    ) {
                        for (comparator in listOf(
                            GradeComparator.ExactGradeComparator(),
                            GradeComparator.AtLeastGradeComparator(),
                            GradeComparator.AtMostGradeComparator(),
                        )) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .semantics {
                                        contentDescription = "Comparator"
                                    },
                                enabled = true,
                                text = {
                                    Text(stringResource(comparator.word))
                                },
                                onClick = {
                                    comparatorDropdownExpanded = false
                                    onFilterUpdate(
                                        // Idea: use a .copy() instead?
                                        ProposalGradeBallotsFilter(
                                            proposalIndex = filter.proposalIndex,
                                            gradeIndex = filter.gradeIndex,
                                            comparator = comparator,
                                        )
                                    )
                                },
                            )
                        }
                    }

                    TextButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .heightIn(min = buttonMinHeight)
                            .widthIn(min = buttonMinWidth)
                            .semantics {
                                onClick(
                                    label = "pick a grade for this filter",
                                    action = null,
                                )
                            },
                        shape = MaterialTheme.shapes.small,
                        contentPadding = buttonPadding,
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
                                            comparator = filter.comparator,
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
