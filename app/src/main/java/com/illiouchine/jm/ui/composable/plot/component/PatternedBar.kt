package com.illiouchine.jm.ui.composable.plot.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.illiouchine.jm.ui.theme.Theme

/**
 * A colored bar with a colored pattern, for use in plots.
 */
@Composable
fun PatternedBar(
    patternBrush: Brush,
    color: Color,
    modifier: Modifier = Modifier,
    brush: Brush? = null,
    shape: Shape = RectangleShape,
    border: BorderStroke? = null,
    label: String = "",
) {
    // Background (usually a SolidColor brush)
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .background(
                brush = if (brush != null) {
                    brush
                } else {
                    SolidColor(value = color)
                },
                shape = shape,
            )
            .clip(shape),
    )
    // Pattern (for accessibility)
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                drawRect(
                    brush = patternBrush,
                    colorFilter = ColorFilter.tint(
                        color = lerp(
                            start = color,
                            stop = Color.Black,
                            fraction = 0.2f,
                        ),
                    ),
                )
            }
            .clip(shape),
    )

    if (label.isNotEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val labelModifier = Modifier.align(alignment = Alignment.BottomCenter)
            val labelStyle = Theme.typography.bodyMedium
            // Outline
            Text(
                modifier = labelModifier,
                text = label,
                style = labelStyle.copy(
                    color = Theme.colorScheme.background,
                    drawStyle = Stroke(
                        width = 6f,
                        join = StrokeJoin.Round,
                    ),
                ),
            )
            // Fill
            Text(
                modifier = labelModifier,
                text = label,
                style = labelStyle,
            )
        }
    }
}