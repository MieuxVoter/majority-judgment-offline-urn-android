package com.illiouchine.jm.ui.composable.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
fun SpirographMenu(
    modifier: Modifier,
    spirograph: Spirograph,
    onSaveSpirograph: (spirograph: Spirograph) -> Unit = {},
    onAddCompass: (spirograph: Spirograph) -> Unit = {},
){
    LazyColumn(modifier = modifier.padding(16.dp)) {
        itemsIndexed(spirograph.epicycloid.compasses){ index, compass ->
            CompassMenu(
                modifier = Modifier,
                compassIndex = index,
                compass = compass
            )
        }
        item {
            TextButton(
                modifier = Modifier,
                onClick = { onAddCompass(spirograph) }
            ) {
                Icon(Icons.Filled.Add, "Add Compass")
                Text("Add Compass")
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ){
                Button(
                    modifier = Modifier,
                    onClick = { onSaveSpirograph(spirograph) }
                ) {
                    Icon(Icons.Filled.Done, "Save")
                    Text("Save")
                }
            }
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

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$compassIndex# - Compass")
            IconButton(
                modifier = Modifier.graphicsLayer { alpha = 0.2f },
                onClick = { onDeleteCompass() }
            ) { Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red) }
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
                valueRange = 0f..12f,
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
            )
        }

    }
}


@Preview(showSystemUi = true)
@Composable
private fun SpirographMenuPreview() {
    JmTheme {
        SpirographMenu(
            modifier = Modifier.fillMaxSize(),
            spirograph = Spirograph(epicycloid = epicycloids.first()),
            onSaveSpirograph = {}
        )
    }
}