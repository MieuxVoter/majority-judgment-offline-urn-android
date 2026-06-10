package com.illiouchine.jm.ui.utils

import java.io.ByteArrayOutputStream
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater

fun compress(input: ByteArray): ByteArray {
    val deflater = Deflater()
    deflater.setInput(input)
    deflater.finish()

    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)

    while (!deflater.finished()) {
        val compressedSize = deflater.deflate(buffer)
        outputStream.write(buffer, 0, compressedSize)
    }

    return outputStream.toByteArray()
}

@Throws(DataFormatException::class)
fun decompress(input: ByteArray): ByteArray {
    val inflater = Inflater()
    inflater.setInput(input)

    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)

    while (!inflater.finished()) {
        val decompressedSize = inflater.inflate(buffer)
        outputStream.write(buffer, 0, decompressedSize)
    }

    return outputStream.toByteArray()
}
