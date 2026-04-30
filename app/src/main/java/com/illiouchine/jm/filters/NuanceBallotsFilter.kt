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
import kotlin.math.min

@Serializable
data class NuanceBallotsFilter(
    val comparatorIndex: Int,
    val nuance: Int,
) : BallotsFilterInterface {

    val comparators = listOf(
        IntegerComparator.ExactIntegerComparator(),
        IntegerComparator.AtLeastIntegerComparator(),
        IntegerComparator.AtMostIntegerComparator(),
    ).toPersistentList()

    override fun shouldKeep(ballot: Ballot): Boolean {
        return comparators[comparatorIndex].compare(
            ballot.getNuance(),
            nuance,
        )
    }

    override fun render(
        poll: Poll,
        onFilterDelete: () -> Unit,
        onFilterUpdate: (BallotsFilterInterface) -> Unit,
    ): @Composable (() -> Unit) {
        val filter = this
        val maximumNuance = min(
            poll.pollConfig.grading.getAmountOfGrades(),
            poll.pollConfig.proposals.size,
        )

        return (
            @Composable {
                BallotsFilter(
                    onFilterDelete = onFilterDelete,
                ) {
                    TextInlinedWithTextButton(
                        text = "use",
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
                        currentValueIndex = nuance - 1,
                        values = List(size = maximumNuance) { i ->
                            (i + 1).toString()
                        }.toPersistentList(),
                        onClickLabel = "pick a nuance for this filter",
                        onChange = {
                            onFilterUpdate(
                                filter.copy(
                                    nuance = it + 1,
                                )
                            )
                        },
                    )

                    TextInlinedWithTextButton(
                        text = "different grades",
                    )
                }
            }
            )
    }
}
