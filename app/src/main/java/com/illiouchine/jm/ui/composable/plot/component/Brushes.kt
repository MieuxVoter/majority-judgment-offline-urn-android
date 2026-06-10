package com.illiouchine.jm.ui.composable.plot.component

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.illiouchine.jm.R

fun getImageBitmapFromVectorImage(
    res: Resources,
    @DrawableRes id: Int,
): ImageBitmap {
    // Android Studio suggested use of resourceCompat instead of LocalResources.current.getDrawable
    // Also, perhaps we should wrap this in a throw/catch and serve a blank bitmap as fallback?
    return ResourcesCompat.getDrawable(
        /* res = */
        res,
        /* id = */
        id,
        /* theme = */
        null,
    )!!.toBitmap().asImageBitmap()
}

@Composable
fun getPatternBrushes(): List<ShaderBrush> {
    val res = LocalResources.current
    val blank = ImageBitmap.imageResource(R.drawable.blank)

    return remember {
        val patterns = listOf(
            blank,
            getImageBitmapFromVectorImage(res, R.drawable.hexagon_pattern_01),
            getImageBitmapFromVectorImage(res, R.drawable.hexagon_pattern_02),
            getImageBitmapFromVectorImage(res, R.drawable.hexagon_pattern_03),
            getImageBitmapFromVectorImage(res, R.drawable.hexagon_pattern_04),
            getImageBitmapFromVectorImage(res, R.drawable.hexagon_pattern_05),
            getImageBitmapFromVectorImage(res, R.drawable.hexagon_pattern_06),
        )
        listOf(
            ShaderBrush(
                shader = ImageShader(
                    image = patterns[0],
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                ),
            ),
            ShaderBrush(
                shader = ImageShader(
                    image = patterns[1],
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                ),
            ),
            ShaderBrush(
                shader = ImageShader(
                    image = patterns[2],
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                ),
            ),
            ShaderBrush(
                shader = ImageShader(
                    image = patterns[3],
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                ),
            ),
            ShaderBrush(
                shader = ImageShader(
                    image = patterns[4],
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                ),
            ),
            ShaderBrush(
                shader = ImageShader(
                    image = patterns[5],
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                ),
            ),
            ShaderBrush(
                shader = ImageShader(
                    image = patterns[6],
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                ),
            ),
        )
    }
}
