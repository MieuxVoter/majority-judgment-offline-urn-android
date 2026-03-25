package com.illiouchine.jm.ui.composable.plot

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.service.ProximityAnalysis
import com.illiouchine.jm.service.ProximityAnalyzer
import com.illiouchine.jm.ui.composable.plot.lib.SpiderChart
import com.illiouchine.jm.ui.composable.plot.utils.truncate
import com.illiouchine.jm.ui.preview.PreviewDataFaker
import com.illiouchine.jm.ui.theme.JmTheme
import java.lang.Integer.min

// Shows how close (in the collective hearts of the judges) pairs of proposals are.
// A proximity of 1 means that the two proposals received exactly the same grades in each ballot.
// A proximity of 0 means that the two proposals received extreme and diametrically opposite grades in each ballot.
// Basically:    proximity = 1.0 - standardDeviation / maximumStandardDeviation
// This assumes that grades are somewhat linearly distributed, value-wise.
@Composable
fun ProximitySpider(
    modifier: Modifier = Modifier,
    analysis: ProximityAnalysis,
    onlyProposalsIndices: List<Int>? = null,
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val allProposalsIndices = 0.rangeUntil(analysis.proposals.size).toList()
    val lotsOfProposalsIndices = onlyProposalsIndices ?: allProposalsIndices

    val maxAmountOfProposalsThatFit = 16
    val maxAmountOfProposals = min(maxAmountOfProposalsThatFit, lotsOfProposalsIndices.size)

    val proposalsIndices = lotsOfProposalsIndices.slice(0..<maxAmountOfProposals)
    val filteredAnalysis = analysis.filterByProposalsIndices(proposalsIndices)

    SpiderChart(
        modifier = modifier,
//        title = {},
        categories = filteredAnalysis.proposals.map { it.truncate(16, "…") },
        values = filteredAnalysis.proximities[selectedCategoryIndex].map { it.toFloat() },
        tickValues = listOf(-1f, 0f, 1f),
        tickDecimals = 0,
        highlightedCategoryIndex = selectedCategoryIndex,
        onCategoryClick = { clickedCategoryIndex ->
            @Suppress("AssignedValueIsNeverRead") // TBD: why?
            selectedCategoryIndex = clickedCategoryIndex
        },
    )

}


@Preview(
    name = "Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Preview(
    name = "Phone (Landscape)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
    fontScale = 1.0f,
)
@Composable
fun PreviewProximitySpider(modifier: Modifier = Modifier) {
    val poll = PreviewDataFaker.poll(
        amountOfBallots = 7,
        amountOfProposals = 12,
    )
    val analyzer = ProximityAnalyzer()
    val analysis = analyzer.analyze(
        poll = poll,
    )
    JmTheme {
        Column(modifier) {
            Text("\uD83E\uDD9D I am the glitch raccoon that looks for glitches:")
//            PlotTitle("Proximity Spider\n${poll.pollConfig.subject}")
            ProximitySpider(
                modifier = Modifier.height(400.dp),
                analysis = analysis,
            )
        }
    }
}
