package com.illiouchine.jm.ui.composable.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun EpicycloidMenu(
    modifier: Modifier,
    epicycloid: Epicycloid,
    onSaveEpicycloid: (epicycloid: Epicycloid) -> Unit = {},
) {
    var name by remember { mutableStateOf(epicycloid.name) }
    var compasses by remember { mutableStateOf(epicycloid.compasses) }

    LazyColumn(modifier = modifier.padding(24.dp)) {
        item {
            TextField(
                label = { },
                modifier = modifier.fillMaxWidth(),
                maxLines = 1,
                singleLine = true,
                placeholder = { Text("Add a name") },
                value = name,
                onValueChange = { name = it },
            )
        }


        itemsIndexed(compasses, key = { _, compass -> compass.hashCode() }) { index, compass ->
            CompassMenu(
                modifier = Modifier.animateItem(),
                compassIndex = index,
                compass = compass,
                onDeleteCompass = {
                    val newCompasses = compasses.toMutableList()
                    newCompasses.removeAt(index)
                    compasses = newCompasses.toList()
                },
                onChangeRadius = { radius ->
                    val newCompasses = compasses.toMutableList()
                    val newCompass = newCompasses[index].copy(radius = radius)
                    newCompasses[index] = newCompass
                    compasses = newCompasses.toList()
                },
                onChangeSpeed = { speed ->
                    val newCompasses = compasses.toMutableList()
                    val newCompass = newCompasses[index].copy(speed = speed)
                    newCompasses[index] = newCompass
                    compasses = newCompasses.toList()
                },
                onChangePhase = { phase ->
                    val newCompasses = compasses.toMutableList()
                    val newCompass = newCompasses[index].copy(phase = phase)
                    newCompasses[index] = newCompass
                    compasses = newCompasses.toList()
                },
            )
        }
        item {
            TextButton(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    // Initialize new compass
                    val newCompasses = compasses.toMutableList()
                    val newCompass = Compass(0.0)
                    newCompasses.add(newCompass)
                    compasses = newCompasses.toList()
                }
            ) {
                Icon(Icons.Filled.Add, "Add Compass")
                Text("Add Compass")
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    modifier = Modifier,
                    onClick = {
                        val newEpicycloid = Epicycloid(
                            name = name,
                            compasses = compasses
                        )
                        onSaveEpicycloid(newEpicycloid)
                    }
                ) {
                    Icon(Icons.Filled.Done, "Save")
                    Text("Save")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CompassMenu(
    modifier: Modifier = Modifier,
    compassIndex: Int,
    compass: Compass,
    onDeleteCompass: () -> Unit = {},
    onChangeRadius: (radius: Double) -> Unit = {},
    onChangeSpeed: (speed: Double) -> Unit = {},
    onChangePhase: (phase: Double) -> Unit = {},
) {
    var radius by remember { mutableFloatStateOf(compass.radius.toFloat()) }
    var speed by remember { mutableFloatStateOf(compass.speed.toFloat()) }
    var phase by remember { mutableFloatStateOf(compass.phase.toFloat()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$compassIndex# - Compass")
            IconButton(
                modifier = Modifier.graphicsLayer { alpha = 0.6f },
                onClick = { onDeleteCompass() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Radius")
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = radius,
                onValueChange = { radius = it },
                valueRange = 0f..1f,
                onValueChangeFinished = {
                    onChangeRadius(radius.toDouble())
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Speed")
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = speed,
                onValueChange = { speed = it },
                valueRange = -11f..11f,
                steps = 21,
                onValueChangeFinished = {
                    onChangeSpeed(speed.toDouble())
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Phase")
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = phase,
                onValueChange = { phase = it },
                valueRange = 0f..1f,
                onValueChangeFinished = {
                    onChangePhase(phase.toDouble())
                }
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun EpicycloidMenuPreview() {
    JmTheme {
        EpicycloidMenu(
            modifier = Modifier.fillMaxSize(),
            epicycloid = defaultsEpicycloids.first(),
            onSaveEpicycloid = {}
        )
    }
}
