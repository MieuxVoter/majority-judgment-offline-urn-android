package com.illiouchine.jm.ui.composable.loading

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun LoaderScreen(
    modifier: Modifier = Modifier,
) {
    var currentEpicycloidPresetIndex by remember { mutableIntStateOf(0) }
    var spirograph by remember { mutableStateOf(
        Spirograph(epicycloid = epicycloids[currentEpicycloidPresetIndex])
    ) }

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (showMenu){
                IconButton(
                    modifier = Modifier.graphicsLayer { alpha = 0.2f },
                    onClick = { showMenu = false }
                ) { Icon(Icons.Filled.Close, "Close") }
            } else {
                IconButton(
                    modifier = Modifier.graphicsLayer { alpha = 0.2f },
                    onClick = { showMenu = true }
                ) { Icon(Icons.Filled.Settings, "Settings") }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            if (showMenu) {
                SpirographMenu(
                    modifier = Modifier.fillMaxSize(),
                    spirograph = spirograph,
                    onSaveSpirograph = {},
                    onAddCompass = {},
                )
            } else {
                Loader(
                    modifier = modifier.fillMaxSize(),
                    spirograph = spirograph,
                    onNextSpirograph = {
                        currentEpicycloidPresetIndex = (currentEpicycloidPresetIndex + 1) % epicycloids.size
                    },
                    onChangeCompassValue = { index,radius,speed ->
                        val oldCompass = spirograph.epicycloid.compasses[index]
                        val newCompass = oldCompass.copy(
                            radius = radius.coerceIn(0.0, 1.0),
                            speed = speed.coerceIn(0.0, 5.0)
                        )
                        Log.d("wgu", "$newCompass")
                        spirograph = spirograph.copy(
                            epicycloid = spirograph.epicycloid.copy(
                                compasses = spirograph.epicycloid.compasses.mapIndexed{ compassIndex, compass ->
                                    if (compassIndex == index){
                                        newCompass
                                    } else {
                                        compass
                                    }
                                }
                            )
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun Loader(
    modifier: Modifier = Modifier,
    spirograph: Spirograph = Spirograph(epicycloid = epicycloids.first()),
    onNextSpirograph: () -> Unit = {},
    onChangeCompassValue: (index: Int, radius: Double, speed: Double) -> Unit = { _,_,_ ->  },
) {
    val grading = Grading.Quality7Grading
    val amountOfOrbitals = grading.getAmountOfGrades()
    val trailLength = 7
    val trailDelay = 0.0007

    var currentCompassPresetIndex by remember { mutableIntStateOf(0) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

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
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // change epicycloid (recharge full config from preset)
                        onNextSpirograph()
                        Log.d(
                            "WGU",
                            "doubleTAP : change epicycloid (recharge full config from preset)"
                        )
                    },
                    onLongPress = {
                        // onLongPress = TODO(), // save
                    },
                    onTap = {
                        // change current targeted compass
                        currentCompassPresetIndex =
                            (currentCompassPresetIndex + 1) % spirograph.epicycloid.compasses.size
                        Log.d(
                            "WGU",
                            "onTap : change current targeted compass $currentCompassPresetIndex"
                        )
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        Log.d("WGU", "----------------")

                        Log.d("WGU", "Drag value  = $offsetX : $offsetY")

                        val compass = spirograph.epicycloid.compasses[currentCompassPresetIndex]
                        Log.d("WGU", "radius : ${compass.radius}")
                        Log.d("WGU", "speed : ${compass.speed}")


                        val radiusRatio = (offsetX / size.width)
                        Log.d("WGU", "radiusRatio : ${offsetX}/${size.width} = $radiusRatio")
                        val speedRatio = (offsetY / size.height)
                        Log.d("WGU", "speedRatio : ${offsetY}/${size.height} = $speedRatio")


                        onChangeCompassValue(
                            currentCompassPresetIndex,
                            compass.radius + radiusRatio,
                            compass.speed + speedRatio
                        )
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    Log.d("WGU", "dragAmount : ${dragAmount.x} ${dragAmount.y}")
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
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
        LoaderScreen(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}