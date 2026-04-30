package com.illiouchine.jm.filters

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.BallotsFilter
import com.illiouchine.jm.ui.composable.button.TextButtonWithDropdown
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

@Serializable
data class ProposalGradeBallotsFilter(
    val proposalIndex: Int,
    val gradeIndex: Int,
    val comparatorIndex: Int,
) : BallotsFilterInterface {

    val comparators = listOf(
        IntegerComparator.ExactIntegerComparator(),
        IntegerComparator.AtLeastIntegerComparator(),
        IntegerComparator.AtMostIntegerComparator(),
    ).toPersistentList()

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
        return (
            @Composable {
                BallotsFilter(
                    onFilterDelete = onFilterDelete,
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
            }
            )
    }
}
