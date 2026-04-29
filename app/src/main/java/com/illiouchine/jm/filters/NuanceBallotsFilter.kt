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
                        text = "using",
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
                        currentValueIndex = nuance,
                        values = List(poll.pollConfig.grading.grades.size) { i ->
                            i.toString()
                        }.toPersistentList(),
                        onClickLabel = "pick a nuance for this filter",
                        onChange = {
                            onFilterUpdate(
                                filter.copy(
                                    nuance = it,
                                )
                            )
                        },
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = "different grades",
                        fontSize = fontSizeTextButton,
                    )
                }
            }
            )
    }
}
