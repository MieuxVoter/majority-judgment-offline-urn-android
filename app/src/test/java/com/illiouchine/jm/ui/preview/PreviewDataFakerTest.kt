package com.illiouchine.jm.ui.preview

import org.junit.Assert.assertEquals
import org.junit.Test

class PreviewDataFakerTest {
    @Test
    fun fakeSomeJudgments() {
        (0..10).forEach { i ->
            val judgments = PreviewDataFaker.judgments(size = i)
            assertEquals(i, judgments.size)
        }
    }

    @Test
    fun buildPollConfig() {
        (0..10).forEach { i ->
            val pollConfig = PreviewDataFaker.pollConfig()
            assert(pollConfig.subject.isNotEmpty())
            assert(pollConfig.proposals.isNotEmpty())
        }
    }
}
