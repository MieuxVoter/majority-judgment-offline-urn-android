package com.illiouchine.jm.filters

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.button.TextButtonWithDropdown
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

@Serializable
sealed class GradeComparator(
    @get:StringRes val word: Int,
    val compare: (ballotGrade: Int, grade: Int) -> Boolean,
) {

    class ExactGradeComparator : GradeComparator(
        word = R.string.grade_comparator_exactly,
        compare = { ballotGrade, grade ->
            ballotGrade == grade
        },
    )

    class AtLeastGradeComparator : GradeComparator(
        word = R.string.grade_comparator_at_least,
        compare = { ballotGrade, grade ->
            ballotGrade >= grade
        },
    )

    class AtMostGradeComparator : GradeComparator(
        word = R.string.grade_comparator_at_most,
        compare = { ballotGrade, grade ->
            ballotGrade <= grade
        },
    )

}

val comparators = listOf(
    GradeComparator.ExactGradeComparator(),
    GradeComparator.AtLeastGradeComparator(),
    GradeComparator.AtMostGradeComparator(),
).toPersistentList()

@Serializable
data class ProposalGradeBallotsFilter(
    val proposalIndex: Int,
    val gradeIndex: Int,
    val comparatorIndex: Int,
) : BallotsFilterInterface {

    override fun shouldKeep(ballot: Ballot): Boolean {
        return comparators[comparatorIndex].compare(
            ballot.gradeOf(proposalIndex),
            gradeIndex,
        )
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
                    // Adapt the raw text in-between buttons to the size of the buttons
                    val fontSizeTextButton = 14.sp

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = "judge",
                        fontSize = fontSizeTextButton,
                    )

                    TextButtonWithDropdown(
                        currentValueIndex = proposalIndex,
                        values = poll.pollConfig.proposals.toPersistentList(),
                        onClickLabel = "pick a proposal for this filter",
                        onChange = {
                            onFilterUpdate(
                                filter.copy(
                                    proposalIndex = it,
                                )
                            )
                        },
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = "to be",
                        fontSize = fontSizeTextButton,
                    )

                    TextButtonWithDropdown(
                        currentValueIndex = comparatorIndex,
                        values = comparators.map {
                            stringResource(it.word)
                        }.toPersistentList(),
                        onClickLabel = "pick a comparator for this filter",
                        onChange = {
                            onFilterUpdate(
                                filter.copy(
                                    comparatorIndex = it,
                                )
                            )
                        },
                    )

                    TextButtonWithDropdown(
                        currentValueIndex = gradeIndex,
                        values = List(poll.pollConfig.grading.grades.size) { i ->
                            stringResource(
                                poll.pollConfig.grading.getGradeName(i),
                            )
                        }.toPersistentList(),
                        onClickLabel = "pick a grade for this filter",
                        onChange = {
                            onFilterUpdate(
                                filter.copy(
                                    gradeIndex = it,
                                )
                            )
                        },
                    )
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
