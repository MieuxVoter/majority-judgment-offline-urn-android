package com.illiouchine.jm.ui.composable.shape

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Stable
class PathShape : Shape {

    private val path: Path

    constructor(p: Path) {
        this.path = p
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val pathMatrix = Matrix()
        pathMatrix.scale(
            x = size.width,
            y = size.height,
        )

        val scaledPath = path.copy()
        scaledPath.transform(pathMatrix)

        return Outline.Generic(scaledPath)
    }

    override fun toString(): String = "PathShape($path)"
}
