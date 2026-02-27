package com.illiouchine.jm.logic


fun List<Double>.reversedIf(shouldReverse: Boolean): List<Double> {
    if (shouldReverse) {
        return this.reversed()
    }

    return this
}
