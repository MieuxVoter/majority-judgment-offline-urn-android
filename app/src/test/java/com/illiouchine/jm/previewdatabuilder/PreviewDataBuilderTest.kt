package com.illiouchine.jm.previewdatabuilder

import com.illiouchine.jm.ui.preview.PreviewDataFaker
import org.junit.Assert.assertEquals
import org.junit.Test

class PreviewDataBuilderTest {
    @Test
    fun buildJudgment() {
        // Test judgments size.
        (0..10).forEach { i ->
            val judgments = PreviewDataFaker.judgments(size = i)
            assertEquals(i, judgments.size)
        }
    }

    @Test
    fun buildPollConfig() {
        // Test judgments size.
        (0..10).forEach { i ->
            val pollConfig = PreviewDataFaker.pollConfig()
            assert(pollConfig.subject.isNotEmpty())
            assert(pollConfig.proposals.isNotEmpty())
        }
    }
}
