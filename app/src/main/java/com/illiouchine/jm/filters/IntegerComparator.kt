package com.illiouchine.jm.filters

import androidx.annotation.StringRes
import com.illiouchine.jm.R
import kotlinx.serialization.Serializable

@Serializable
sealed class IntegerComparator(
    @get:StringRes val word: Int,
    val compare: (base: Int, value: Int) -> Boolean,
) {

    class ExactIntegerComparator : IntegerComparator(
        word = R.string.grade_comparator_exactly,
        compare = { base, value ->
            base == value
        },
    )

    class AtLeastIntegerComparator : IntegerComparator(
        word = R.string.grade_comparator_at_least,
        compare = { base, value ->
            base >= value
        },
    )

    class AtMostIntegerComparator : IntegerComparator(
        word = R.string.grade_comparator_at_most,
        compare = { base, value ->
            base <= value
        },
    )
}
