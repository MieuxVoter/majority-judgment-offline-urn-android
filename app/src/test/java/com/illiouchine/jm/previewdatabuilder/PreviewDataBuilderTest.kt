package com.illiouchine.jm.previewdatabuilder

import com.illiouchine.jm.ui.previewdatabuilder.PreviewDataBuilder
import com.illiouchine.jm.ui.previewdatabuilder.PreviewDataBuilder.judgments
import org.junit.Assert.assertEquals
import org.junit.Test

class PreviewDataBuilderTest {
    @Test
    fun buildJudgment() {
        // Test judgments size.
        (0..10).forEach { i->
            val judgments = PreviewDataBuilder.judgments(size = i)
            assertEquals(i, judgments.size)
        }
    }

    @Test
    fun buildPollConfig() {
        // Test judgments size.
        (0..10).forEach { i->
            val pollConfig = PreviewDataBuilder.pollConfig()
            assert(pollConfig.subject.isNotEmpty())
            assert(pollConfig.proposals.isNotEmpty())
        }
    }
}