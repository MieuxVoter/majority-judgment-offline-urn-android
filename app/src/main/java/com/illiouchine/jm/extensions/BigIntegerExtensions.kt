package com.illiouchine.jm.extensions

import com.ionspin.kotlin.bignum.integer.BigInteger

fun <T> Iterable<T>.bigSumOf(selector: (T) -> BigInteger): BigInteger {
    var sum: BigInteger = BigInteger.ZERO
    for (element in this) {
        sum = sum.add(selector(element))
    }
    return sum
}
