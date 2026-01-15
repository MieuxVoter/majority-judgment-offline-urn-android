package com.illiouchine.jm.ui.previewdatabuilder

import com.illiouchine.jm.model.Judgment
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.floor

object PreviewDataBuilder{
    fun judgments(size: Int = 3): ImmutableList<Judgment> {
        val judgments = mutableListOf<Judgment>()
        0.rangeUntil(size).forEach { i ->
            val judgment = Judgment(proposal = i, grade = floor(Math.random() * 7).toInt() )
            judgments.add(judgment)
        }
        return judgments.toImmutableList()
    }
}