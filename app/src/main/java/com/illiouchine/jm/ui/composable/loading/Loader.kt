package com.illiouchine.jm.ui.composable.loading

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
    var epicycloids by remember { mutableStateOf(defaultsEpicycloids) }
    var currentEpicycloidPresetIndex by remember { mutableIntStateOf(0) }

    val spirograph by remember {
        derivedStateOf {
            Spirograph(epicycloid = epicycloids[currentEpicycloidPresetIndex])
        }
    }

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (showMenu) {
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
        Box(modifier = Modifier.padding(innerPadding)) {
            if (showMenu) {
                EpicycloidMenu(
                    modifier = Modifier.fillMaxSize(),
                    epicycloid = spirograph.epicycloid,
                    onSaveEpicycloid = { epicycloid ->
                        val newListOfEpicycloid = epicycloids.toMutableList()
                        val exist: Epicycloid? =
                            newListOfEpicycloid.firstOrNull { it.name == epicycloid.name }
                        if (exist != null) {
                            newListOfEpicycloid.remove(exist)
                        }
                        //newListOfEpicycloid.add(epicycloid)
                        epicycloids = buildList {
                            add(epicycloid)
                            addAll(newListOfEpicycloid)
                        }
                        currentEpicycloidPresetIndex = 0
                        showMenu = false
                    },
                )
            } else {
                Loader(
                    modifier = modifier.fillMaxSize(),
                    spirograph = spirograph,
                    onNextSpirograph = {
                        currentEpicycloidPresetIndex =
                            (currentEpicycloidPresetIndex + 1) % epicycloids.size
                    },
                )
            }
        }
    }
}

@Composable
fun Loader(
    modifier: Modifier = Modifier,
    spirograph: Spirograph = Spirograph(epicycloid = defaultsEpicycloids.first()),
    onNextSpirograph: () -> Unit = {},
) {
    val grading = Grading.Quality7Grading
    val amountOfOrbitals = grading.getAmountOfGrades()
    val trailLength = 7
    val trailDelay = 0.0007

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
                        // TODO show some toast
                    },
                )
            }
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