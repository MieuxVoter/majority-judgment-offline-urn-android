package com.illiouchine.jm.ui.utils

import kotlin.io.encoding.Base64

fun encode(bytes: ByteArray): String {
    return encodeBase64(bytes)
}

@Throws(IllegalArgumentException::class)
fun decode(string: String): ByteArray {
    return decodeBase64(string)
}

// ——— Base 64 (RFC 4648) ——————————————————————————————————————————————————————————————————————————

// We don't have to be url-safe, be AFAIK it does not cost us much, and it's nicely future-proof.
internal val base64Codec = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

internal fun encodeBase64(bytes: ByteArray): String {
    return base64Codec.encode(bytes)
}

internal fun decodeBase64(string: String): ByteArray {
    return base64Codec.decode(string)
}

// ——— Higher Bases ————————————————————————————————————————————————————————————————————————————————

// Here's a very interesting reading : https://blog.kevinalbs.com/base122
// Base122 might totally work for us, since we compress before we encode.
// I'm not sure that we'd gain much by using an even higher-based encoding, given how UTF-8 works.
// Maybe.  Maybe not.  Depends on the internals of our QR Code generator as well.
// Feel free to experiment and report back to us, whatever the result is.   :)
