package com.illiouchine.jm.ui.composable.image

import android.content.Intent
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.min
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File


@Composable
fun QrCodeImage(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
) {
    val winSize = LocalWindowInfo.current.containerDpSize
    val activity = LocalActivity.current
    val context = LocalContext.current

    // Note: Android queues clicks, so using a Boolean for this throttle does not work as expected.
    var lastClickTime by remember { mutableLongStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Image(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .size(min(winSize.width, winSize.height) * 0.9f)
            .clickable(
                enabled = true,
                onClickLabel = "Share this QR Code",
                onClick = {
                    if (activity == null) {
                        return@clickable
                    }

                    // Cheap throttle ; I'm open to more elegant and working solutions.
                    if (lastClickTime + 2000 > SystemClock.uptimeMillis()) {
                        return@clickable
                    }
                    lastClickTime = SystemClock.uptimeMillis()

                    coroutineScope.launch {
                        val rawBitmap = bitmap.asAndroidBitmap()
                        val stream = ByteArrayOutputStream()
                        rawBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val byteArray = stream.toByteArray()

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("image/png")

                        // Empty the QR cache dir, first.
                        val qrCacheDir = File(
                            activity.cacheDir,
                            "qr",
                        )
                        qrCacheDir.deleteRecursively()
                        qrCacheDir.mkdir()

                        // Now we can create and write our QR file
                        val file = File(
                            activity.cacheDir,
                            "qr/shared_qr_" + System.currentTimeMillis() + ".png",
                        )
                        file.setWritable(true)
                        file.writeBytes(byteArray)

                        // file.path = /data/user/0/com.illiouchine.jm/cache/qr/shared_qr_1781144984659.png
                        //Log.i("MJ", "File: ${file.path}")

                        // See app/src/main/res/xml/file_provider.xml and AndroidManifest.xml
                        val uri = FileProvider.getUriForFile(
                            context,
                            "com.illiouchine.jm.service.FileProvider",
                            file,
                        )

                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

                        // Uri: content://com.illiouchine.jm.service.FileProvider/qr_cache/shared_qr_1781147368908.png
                        //Log.i("MJ", "Uri: ${uri}")

                        activity.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "", // empty is OK, because it is ignored on ACTION_SEND
                            )
                        )
                    }
                },
            ),
        bitmap = bitmap,
        contentDescription = "QR Code",
    )
}