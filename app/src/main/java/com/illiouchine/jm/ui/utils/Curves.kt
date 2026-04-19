package com.illiouchine.jm.ui.utils

import androidx.core.math.MathUtils.clamp

fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
    if (edge1 == edge0) return 0f
    val value = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f)
    return value * value * (3.0f - 2.0f * value)
}

/**
 * Inverse of linear interpolation.
 */
fun linearStep(x: Float, x0: Float, x1: Float): Float {
    if (x1 == x0) return 0f
    return (x - x0) / (x1 - x0)
}
