package com.illiouchine.jm.ui.composable.plot.utils

import ir.ehsannarmani.compose_charts.models.Bars
import java.util.stream.IntStream.range


fun AdaptLineCount(maxValue: Int, minValue: Int = 0, maxLineCount: Int = 11): Int {
    val rangeValue = maxValue - minValue
    for (n in range(2, maxLineCount).toArray().reversed()) {
        if (rangeValue % n == 0) {
            return n + 1
        }
    }

    return 5 // the default value in compose charts is 5
}

fun AdaptLineCountForBars(bars: List<Bars>, maxLineCount: Int = 11): Int {
    var maxValue = 0
    bars.forEach {
        it.values.forEach {
            if (it.value > maxValue) {
                maxValue = it.value.toInt()
            }
        }
    }
    return AdaptLineCount(
        maxValue = maxValue,
        minValue = 0,
        maxLineCount = maxLineCount,
    )
}