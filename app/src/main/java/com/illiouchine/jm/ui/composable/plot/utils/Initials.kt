package com.illiouchine.jm.ui.composable.plot.utils

import com.illiouchine.jm.extensions.shortenNames
import com.illiouchine.jm.service.ProximityAnalysis

fun makeProposalsInitials(analysis: ProximityAnalysis): List<String> {
    return analysis.proposals.shortenNames().map {
        it.truncate(
            maxLength = 7, // check big fonts on small screens if you increment this
            ellipsis = "…",
        )
    }
}
