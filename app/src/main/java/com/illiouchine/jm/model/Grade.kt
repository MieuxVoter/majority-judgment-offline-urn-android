package com.illiouchine.jm.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

data class Grade(
    @StringRes val name: Int,
    val color: Color,
    val textColor: Color
)