package com.illiouchine.jm.ui.composable.plot.utils

import ir.ehsannarmani.compose_charts.models.Bars
import java.util.stream.IntStream.range


fun favorIntLineCount(maxValue: Int, minValue: Int = 0, maxLineCount: Int = 11): Int {
    val rangeValue = maxValue - minValue
    if (rangeValue < 2) {
        return 2
    }
    for (n in range(2, maxLineCount).toArray().reversed()) {
        if (rangeValue % n == 0) {
            return n + 1
        }
    }

    return 5 // the default value in compose charts is 5
}



fun favorIntLineCountForBars(bars: List<Bars>, maxLineCount: Int = 11): Int {
    var maxValue = 0
    bars.forEach {
        it.values.forEach {
            if (it.value > maxValue) {
                maxValue = it.value.toInt()
            }
        }
    }

    return favorIntLineCount(
        maxValue = maxValue,
        minValue = 0,
        maxLineCount = maxLineCount,
    )
}
