package com.illiouchine.jm.ui.utils

class Base41DecodingException(message: String) : IllegalArgumentException(message)

/**
 * Base41 encoding/decoding, useful for data URIs in QR Codes.
 *
 * Every 2 bytes are packed into 3 characters (41**3 = 68921 >= 65536).  We waste about 5%.
 * The pair of bytes is read little-endian (`x = b0 + 256 * b1`).
 * Least-significant digit first (`x % 41`, `(x / 41) % 41`, `x / 1681`).
 *
 * In case of an input of odd length, the trailing lone byte is 2 characters (41**2 = 1681 >= 256).
 * Valid encoded lengths are therefore those where `length % 3 != 1`.
 *
 * Stuff to do before this can be a proper lib:
 * - change metod signature to be like Base64 and support chunking
 * - kotlin multiplatform (like Base64 does)
 * - (I'm probably not gonne do that)
 *
 * Check out https://github.com/sveljko/base41 which was a source of inspiration.
 */
class Base41 {

    companion object {

        //const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789\$+-.:"
        // Tweaked to get the URN prefix after gzip compression ;)
        // Of course this should be a Bring Your Own Alphabet parameter
        const val ALPHABET = "ABCDEFGHIJKLMXOPQ7ST:VWNYZ0123456R89\$+-.U"

        private const val BASE = 41
        private const val BASE_SQUARED = BASE * BASE

        // ALPHABET's highest code point is 90 (Z), that's why the size is 91
        private val decodeMap = IntArray(91).apply {
            fill(-1)
            ALPHABET.forEachIndexed { index, char -> this[char.code] = index }
        }
    }

    // Note: a worthy impl (lib-wise) would have that signature:
    //encode(source: ByteArray, startIndex: Int = 0, endIndex: Int = source.length)
    fun encode(data: ByteArray): String {
        val out = StringBuilder((data.size / 2) * 3 + (data.size % 2) * 2)
        var i = 0
        while (i + 1 < data.size) {
            val x = (data[i].toInt() and 0xFF) or ((data[i + 1].toInt() and 0xFF) shl 8)
            out.append(ALPHABET[x % BASE])
            out.append(ALPHABET[(x / BASE) % BASE])
            out.append(ALPHABET[x / BASE_SQUARED])
            i += 2
        }
        if (i < data.size) {
            val x = data[i].toInt() and 0xFF
            out.append(ALPHABET[x % BASE])
            out.append(ALPHABET[x / BASE])
        }
        return out.toString()
    }

    // Note: a worthy impl (lib-wise) would have that signature:
    //decode(source: CharSequence, startIndex: Int = 0, endIndex: Int = source.length)
    fun decode(source: String): ByteArray {
        if (source.length % 3 == 1) {
            throw Base41DecodingException(
                "Invalid Base41 length ${source.length}: must not be of the form 3n + 1"
            )
        }

        val out = ByteArray(size = (source.length / 3) * 2 + if (source.length % 3 == 2) 1 else 0)
        var cursorIn = 0
        var cursorOut = 0

        while (cursorIn + 2 < source.length) {
            val value = (
                digit(source, cursorIn) +
                    digit(source, cursorIn + 1) * BASE +
                    digit(source, cursorIn + 2) * BASE_SQUARED
                )
            if (value > 0xFFFF) {
                // We have to check for this "overflow" because (41**3 = 68921 >= 65536)
                throw Base41DecodingException(
                    "Invalid Base41 group \"${
                        source.substring(cursorIn, cursorIn + 3)
                    }\" at index $cursorIn: value $value exceeds 65535"
                )
            }
            out[cursorOut++] = (value and 0xFF).toByte()
            out[cursorOut++] = (value ushr 8).toByte()
            cursorIn += 3
        }

        if (cursorIn < source.length) { // exactly 2 characters remain
            val value = (
                digit(source, cursorIn) +
                    digit(source, cursorIn + 1) * BASE
                )
            if (value > 0xFF) {
                throw Base41DecodingException(
                    "Invalid Base41 trailing group \"${
                        source.substring(cursorIn)
                    }\" at index $cursorIn: value $value exceeds 255"
                )
            }
            out[cursorOut] = value.toByte()
        }

        return out
    }

    private fun digit(text: String, index: Int): Int {
        val c = text[index]
        val value = if (c.code < decodeMap.size) decodeMap[c.code] else -1
        if (value < 0) {
            throw Base41DecodingException("Invalid Base41 character '$c' at index $index")
        }
        return value
    }
}
