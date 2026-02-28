package com.illiouchine.jm.extensions


fun <T> List<T>.reversedIf(shouldReverse: Boolean): List<T> {
    if (shouldReverse) {
        return this.reversed()
    }

    return this
}
