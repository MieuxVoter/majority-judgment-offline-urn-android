package com.illiouchine.jm.model

data class PollResult(
    val poll: Poll,
    val judgments: List<Judgment>,
)
