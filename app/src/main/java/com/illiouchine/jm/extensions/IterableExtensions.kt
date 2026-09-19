package com.illiouchine.jm.extensions

fun <T> Iterable<T>.reversedIf(condition: Boolean): Iterable<T> {
    if (condition) {
        return this.reversed()
    }

    return this
}
