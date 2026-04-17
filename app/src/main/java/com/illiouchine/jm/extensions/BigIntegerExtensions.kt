package com.illiouchine.jm.extensions

import java.math.BigInteger

fun <T> Iterable<T>.bigSumOf(selector: (T) -> BigInteger): BigInteger {
    var sum: BigInteger = BigInteger.valueOf(0)
    for (element in this) {
        sum = sum.add(selector(element))
    }
    return sum
}
