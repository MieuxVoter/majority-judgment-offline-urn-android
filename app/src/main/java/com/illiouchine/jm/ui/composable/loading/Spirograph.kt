package com.illiouchine.jm.ui.composable.loading

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


// Why isn't TAU in kotlin.math ?   …   Sigh.
const val TAU: Double = PI * 2

data class Spirograph(
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

    private fun getPointOnCircle(
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
    val compasses: ImmutableList<Compass>,
) {
    /**
     * Ensure that the sum of the radii of compasses is 1.
     */
    fun normalized(scale: Double = 1.0): Epicycloid {
        assert(scale > 0.0)
        val normalizedCompasses = this.compasses.toMutableList()
        var sumOfRadii = 0.0
        this.compasses.forEach { compass: Compass ->
            sumOfRadii += abs(compass.radius)
        }
        if (sumOfRadii > 0.0) {
            this.compasses.forEachIndexed { index, compass: Compass ->
                val normalizedCompass = compass.copy(
                    radius = scale * compass.radius / sumOfRadii
                )
                normalizedCompasses[index] = normalizedCompass
            }
        }

        return this.copy(
            compasses = normalizedCompasses.toList().toImmutableList(),
        )
    }
}

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


val defaultsEpicycloids: List<Epicycloid> = listOf(
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
        ).toImmutableList(),
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
        ).toImmutableList(),
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
        ).toImmutableList(),
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
        ).toImmutableList(),
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
        ).toImmutableList(),
    ),
)
