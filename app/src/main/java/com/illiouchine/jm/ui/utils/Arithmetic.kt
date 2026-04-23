package com.illiouchine.jm.ui.utils

/**
 * Sums integers from 0 to n.
 */
fun sumTo(n: Int): Int {
    if (n < 0) return -sumTo(-n)
    return ((n + 1) * n) / 2
}
