package com.illiouchine.jm.ui.utils

import kotlin.io.encoding.Base64

fun encode(bytes: ByteArray): String {
    return encodeBase41(bytes)
}

@Throws(IllegalArgumentException::class)
fun decode(string: String): ByteArray {
    return decodeBase41(string)
}

// ——— Base 64 (RFC 4648) ——————————————————————————————————————————————————————————————————————————
// Handy, but does not play well with the internal base45 QR Code encoding.

internal val base64Codec = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

internal fun encodeBase64(bytes: ByteArray): String {
    return base64Codec.encode(bytes)
}

internal fun decodeBase64(string: String): ByteArray {
    return base64Codec.decode(string)
}

// ——— Base 41 —————————————————————————————————————————————————————————————————————————————————————
// Something like https://github.com/sveljko/base41/blob/master/base41.md
// In order to fit in both a QR Code and URI neatly.

internal val base41Codec = Base41()

internal fun encodeBase41(bytes: ByteArray): String {
    return base41Codec.encode(bytes)
}

internal fun decodeBase41(string: String): ByteArray {
    return base41Codec.decode(string)
}

// ——— Higher Bases ————————————————————————————————————————————————————————————————————————————————

// Here's a very interesting reading : https://blog.kevinalbs.com/base122
// I don't think we'd gain by using a higher-based encoding.
