package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.illiouchine.jm.ui.composable.loading.Epicycloid
import com.illiouchine.jm.ui.composable.loading.EpicycloidMenu
import com.illiouchine.jm.ui.composable.loading.Loader
import com.illiouchine.jm.ui.composable.loading.Spirograph
import com.illiouchine.jm.ui.composable.loading.defaultsEpicycloids


@Composable
fun LoaderScreen(
    modifier: Modifier = Modifier,
) {
    var epicycloids by remember { mutableStateOf(defaultsEpicycloids) }
    var currentEpicycloidPresetIndex by remember { mutableIntStateOf(0) }

    val spirograph by remember {
        derivedStateOf {
            Spirograph(epicycloid = epicycloids[currentEpicycloidPresetIndex].normalized(0.93))
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
                        val otherEpicycloids = epicycloids.toMutableList()
                        val exist: Epicycloid? =
                            otherEpicycloids.firstOrNull { it.name == epicycloid.name }
                        if (exist != null) {
                            otherEpicycloids.remove(exist)
                        }
                        epicycloids = buildList {
                            add(epicycloid)
                            addAll(otherEpicycloids)
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