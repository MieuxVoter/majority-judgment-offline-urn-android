package com.illiouchine.jm.ui.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import qrcode.QRCode
import qrcode.color.Colors
import qrcode.raw.ErrorCorrectionLevel

fun renderQrCodePngBytes(qrContent: String): ByteArray {
    return QRCode.ofSquares()
        .withColor(Colors.BLACK)
        .withBackgroundColor(Colors.WHITE)
        .withMargin(60)
        .withSize(30)
        .withInnerSpacing(0)
        .withErrorCorrectionLevel(ErrorCorrectionLevel.LOW)
        .build(qrContent)
        .renderToBytes()
}

fun imageBitmapFromPngBytes(encodedImageData: ByteArray): ImageBitmap {
    return BitmapFactory.decodeByteArray(
        encodedImageData,
        0,
        encodedImageData.size,
    ).asImageBitmap()
}
