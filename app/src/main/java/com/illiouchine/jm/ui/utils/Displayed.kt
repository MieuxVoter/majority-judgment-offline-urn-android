package com.illiouchine.jm.ui.utils

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned


/**
 * The [displayed] extension function detects whether a composable is visible in the window
 * and triggers a callback [onVisibilityChanged] whenever its visibility changes.
 */
fun Modifier.displayed(onVisibilityChanged: (Boolean) -> Unit) = composed {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) { onVisibilityChanged.invoke(isVisible) }
    this.onGloballyPositioned { layoutCoordinates ->
        isVisible = layoutCoordinates.parentLayoutCoordinates?.let { parentLayout ->
            val parentBounds = parentLayout.boundsInWindow()
            val childBounds = layoutCoordinates.boundsInWindow()
            parentBounds.overlaps(childBounds)
        } ?: false
    }
}