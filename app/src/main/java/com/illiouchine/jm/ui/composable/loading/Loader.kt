package com.illiouchine.jm.ui.composable.loading

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.theme.JmTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Why isn't TAU in kotlin.math ?   …   Sigh.
const val TAU: Double = PI * 2

class Spirograph(
    val epicycloid: Epicycloid,
) {

    fun getPoint(
        position: Double,
    ): Point {
        var p = Point(x = 0.0, y = 0.0)
        epicycloid.compasses.forEach { compass ->
            p = getPointOnCircle(
                center = p,
                position = position,
                compass = compass,
            )
        }
        return p
    }

    fun getPointOnCircle(
        center: Point,
        position: Double,
        compass: Compass,
    ): Point {
        val angle = TAU * (position * compass.speed + compass.phase)
        return Point(
            x = center.x + cos(angle) * compass.radius,
            y = center.y + sin(angle) * compass.radius,
        )
    }
}

data class Epicycloid(
    val name: String = "",
    val compasses: List<Compass>,
)

data class Compass(
    val radius: Double,
    val speed: Double = 1.0,
    val phase: Double = 0.0,
)

data class Point(
    val x: Double,
    val y: Double,
) {
    fun toOffset(size: Size): Offset {
        return Offset(
            x = (x.toFloat() + 1.0f) * 0.5f * size.width,
            y = (y.toFloat() + 1.0f) * 0.5f * size.height,
        )
    }
}


val epicycloids: List<Epicycloid> = listOf(
    Epicycloid(
        // i like this one
        name = "Crusty Juggler",
        compasses = listOf(
            Compass(
                radius = 0.20,
            ),
            Compass(
                radius = 0.36,
                speed = 5.0,
            ),
            Compass(
                radius = 0.14,
                speed = 6.0,
            ),
            Compass(
                radius = 0.06,
                speed = 3.0,
            ),
        ),
    ),
    // ring respiration
    Epicycloid(
        name = "Ring Respite",
        compasses = listOf(
            Compass(
                radius = 0.3,
            ),
            Compass(
                radius = 0.08,
                speed = 2.0,
            ),
            Compass(
                radius = 0.25,
                speed = 8.0,
            ),
            Compass(
                radius = 0.05,
                speed = 2.0,
            ),
        ),
    ),
    // Trippy circle
    Epicycloid(
        name = "Trippy Circle",
        compasses = listOf(
            Compass(
                radius = 0.62,
                speed = 2.0,
            ),
            Compass(
                radius = 0.1,
                speed = 2.0,
            ),
            Compass(
                radius = 0.2,
                speed = 12.0,
            ),
        ),
    ),
    // Triangle in square
    Epicycloid(
        name = "Flatland",
        compasses = listOf(
            Compass(
                radius = 0.52,
                speed = 3.0,
            ),
            Compass(
                radius = 0.1,
                speed = 3.0,
            ),
            Compass(
                radius = 0.2,
                speed = 12.0,
            ),
        ),
    ),
    // School of fishes
    Epicycloid(
        name = "Happy Fishes",
        compasses = listOf(
            Compass(
                radius = 0.42,
                speed = 7.0,
            ),
            Compass(
                radius = 0.15,
                speed = -3.0,
            ),
            Compass(
                radius = 0.08,
                speed = 1.0,
            ),
            Compass(
                radius = 0.3,
                speed = 9.0,
            ),
        ),
    ),
)


@Composable
fun Loader(
    modifier: Modifier = Modifier,
) {
    val grading = Grading.Quality7Grading
    val amountOfOrbitals = grading.getAmountOfGrades()
    val trailLength = 7
    val trailDelay = 0.0007
    var currentEpicycloidPresetIndex by remember { mutableIntStateOf(0) }
    val spirograph = Spirograph(
        epicycloid = epicycloids[currentEpicycloidPresetIndex],
    )

    val loopAnimation = remember { Animatable(0f) }

    LaunchedEffect("apparition") {
        loopAnimation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 28000,
                    easing = { x -> x },
                ),
                repeatMode = RepeatMode.Restart,
            ),
        )
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
//                    onDoubleTap = TODO(), // change epicycloid (recharge full config from preset)
//                    onLongPress = TODO(), // save
                    onTap = { // change current targeted compass
                        currentEpicycloidPresetIndex = (currentEpicycloidPresetIndex + 1) % epicycloids.size
                    },
                )

                detectDragGestures(
                    // vertical drag => speed
                    // horizontal drag => radius
                    onDragEnd = {
                    },
                    onDrag = { _, dragAmount ->
                    }
                )
            },
    ) {

        for (i in (0..<amountOfOrbitals)) {
            val position = (
                    loopAnimation.value +
                            i.toDouble() / amountOfOrbitals.toDouble()
                    ).mod(1.0)

            for (trail in (1..trailLength)) {
                drawCircle(
                    color = grading.getGradeColor(i),
                    radius = (10f * (trailLength - trail) / trailLength).dp.toPx(),
                    center = spirograph.getPoint(
                        position = position - (trail * trailDelay),
                    ).toOffset(size),
                    alpha = (1f * (trailLength - trail) / trailLength),
                )
            }

            drawCircle(
                color = grading.getGradeColor(i),
                radius = 10.dp.toPx(),
                center = spirograph.getPoint(
                    position = position,
                ).toOffset(size),
            )
        }

    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PreviewLoader() {
    JmTheme {
        Loader(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}