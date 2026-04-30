package com.illiouchine.jm.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.BallotsFilter
import com.illiouchine.jm.ui.composable.button.TextButtonWithDropdown
import com.illiouchine.jm.ui.composable.button.TextInlinedWithTextButton
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

@Serializable // we might not need this now but at some point this should be Parcelable
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
                    TextInlinedWithTextButton(
                        text = "judge",
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

                    TextInlinedWithTextButton(
                        text = "to be",
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
