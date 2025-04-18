package com.illiouchine.jm.ui.utils

import androidx.core.math.MathUtils.clamp

fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
    val value = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f)
    return value * value * (3.0f - 2.0f * value)
}
