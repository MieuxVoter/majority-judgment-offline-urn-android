package com.illiouchine.jm.extensions

import java.util.Locale
import kotlin.math.floor
import kotlin.math.pow

fun Double.smartFormat(
    maxDecimals: Int = 2,
    locale: Locale = Locale.FRANCE,
): String {
    var decimals = 0
    while (
        decimals < maxDecimals &&
        floor(this * 10.0.pow(decimals)) != this * 10.0.pow(decimals)
    ) {
        decimals += 1
    }
    return String.format(locale, "%.${decimals}f", this)
}
