package com.illiouchine.jm.ui.composable.plot

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.illiouchine.jm.extensions.shortenNames
import com.illiouchine.jm.service.ProximityAnalysis
import com.illiouchine.jm.service.ProximityAnalyzer
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.plot.utils.truncate
import com.illiouchine.jm.ui.composable.shape.PathShape
import com.illiouchine.jm.ui.preview.PreviewDataFaker
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import io.github.koalaplot.core.animation.StartAnimationUseCase
import io.github.koalaplot.core.bar.GroupedHorizontalBarPlot
import io.github.koalaplot.core.bar.horizontalSolidBar
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import java.lang.Integer.min
import kotlin.math.abs

//
// Shows how close (in the collective hearts of the judges) pairs of proposals are.
// A proximity of +1 means that the two proposals received exactly the same grades in each ballot.
// A proximity of -1 means that the two proposals received extreme and diametrically opposite grades in each ballot.
// Basically:    proximity = (0.5 - squaredDeviation / maximumDeviation) * 2.0
// This assumes that grades are somewhat linearly distributed, value-wise.
//

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ProximityBarChart(
    modifier: Modifier = Modifier,
    analysis: ProximityAnalysis,
    proposalsIndices: List<Int>? = null, // show all proposals (unranked and up to 16) if not set
) {
    val density = LocalDensity.current

    // Hoist some colors for use in our non-@Composable lambdas below
    val primaryColor = Theme.colorScheme.primary
    val textColor = Theme.colorScheme.onBackground
    val backgroundColor = Theme.colorScheme.background

    // FIXME: duplicated code with spider
    val allProposalsIndices = 0.rangeUntil(analysis.proposals.size).toList()
    val lotsOfProposalsIndices = proposalsIndices ?: allProposalsIndices

    val maxAmountOfProposalsThatFit = 16
    val maxAmountOfProposals = min(maxAmountOfProposalsThatFit, lotsOfProposalsIndices.size)
    val usedProposalsIndices = lotsOfProposalsIndices.take(maxAmountOfProposals)

    val usedAnalysis = analysis.filterByProposalsIndices(usedProposalsIndices)
    val proposalsInitials = usedAnalysis.proposals.shortenNames().map {
        it.truncate(
            maxLength = 7, // check big fonts on small screens if you increment this
            ellipsis = "…",
        )
    }

    // We need the size to set a default minimum width to our bars (rule: show invisible bars),
    // and we configure the bars via normalized -1...+1 values, and we want responsiveness.
    var chartSize by remember { mutableStateOf(Size.Zero) }

    val tooCloseToOriginThresholdDp = 8.dp
    val tooCloseToOriginThreshold = with(density) {
        if (chartSize.width == 0f) {
            0f
        } else {
            tooCloseToOriginThresholdDp.toPx() / chartSize.width
        }
    }

    XYGraph(
        modifier = modifier.onGloballyPositioned {
            @Suppress("AssignedValueIsNeverRead")
            chartSize = it.size.toSize()
        },
        xAxisModel = rememberFloatLinearAxisModel(-1f..1f, minorTickCount = 0),
        yAxisModel = remember(analysis, proposalsIndices) {
            CategoryAxisModel(
                categories = proposalsInitials.reversed(),
            )
        },
        xAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(all = Theme.spacing.tiny),
                )
            },
            title = {},
        ),
        yAxisContent = AxisContent(
            style = rememberAxisStyle(),
            labels = {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(all = Theme.spacing.tiny),
                )
            },
            title = {},
        ),
    ) {
        GroupedHorizontalBarPlot(
            maxBarGroupWidth = 0.666f,
            startAnimationUseCase = StartAnimationUseCase(
                executionType = StartAnimationUseCase.ExecutionType.Default,
                KoalaPlotTheme.animationSpec,
            ),
        ) {
            val borderStroke = BorderStroke(0.62.dp, backgroundColor)
            val chartProposalsIndices = 0.rangeUntil(proposalsInitials.size).reversed()

            chartProposalsIndices.forEach { categoryIndex ->
                val minimumPossibleProximity = usedAnalysis.minima[categoryIndex].toFloat()
                // val localNeutral = usedAnalysis.neutrals[categoryIndex].toFloat()
                val localNeutral = 0f
                series(
                    defaultBar = horizontalSolidBar(
                        color = textColor,
                        border = borderStroke,
                    ),
                ) {
                    chartProposalsIndices.forEach { proposalIndex ->
                        val name = proposalsInitials[proposalIndex]
                        val value = usedAnalysis.proximities[categoryIndex][proposalIndex].toFloat()
                        val isOwnBar = (categoryIndex == proposalIndex)

                        // Rule: do show a little the "invisible" bars that are too close to zero.
                        val isTooCloseToZero = (abs(value) < tooCloseToOriginThreshold)

                        val xMin = if (isOwnBar) {
                            minimumPossibleProximity
                        } else if (isTooCloseToZero) {
                            if (value == 0f) {
                                -tooCloseToOriginThreshold * 0.5f
                            } else if (value < 0f) {
                                -tooCloseToOriginThreshold
                            } else {
                                0f
                            }
                        } else {
                            0f
                        }

                        val xMax = if (isTooCloseToZero) {
                            if (value == 0f) {
                                tooCloseToOriginThreshold * 0.5f
                            } else if (value > 0f) {
                                tooCloseToOriginThreshold
                            } else {
                                0f
                            }
                        } else {
                            value
                        }

                        item(
                            y = name,
                            xMin = xMin,
                            xMax = xMax,
                            bar = if (isOwnBar) {
                                val notchHalfWidth = 0.015f
                                val notchPosition = ilerp(localNeutral, xMin, xMax)
                                // Perhaps refactor most of this into a NotchedBarShape ?
                                val notchedBarPath = Path()
                                notchedBarPath.moveTo(0f, 0f)
                                notchedBarPath.lineTo(0f, 1f)
                                notchedBarPath.lineTo(notchPosition - notchHalfWidth, 1f)
                                notchedBarPath.lineTo(notchPosition, 0.38f)
                                notchedBarPath.lineTo(notchPosition + notchHalfWidth, 1f)
                                notchedBarPath.lineTo(1f, 1f)
                                notchedBarPath.lineTo(1f, 0f)
                                notchedBarPath.close()

                                horizontalSolidBar(
                                    color = primaryColor,
                                    border = borderStroke,
                                    shape = PathShape(notchedBarPath),
                                )
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Inverse of linear interpolation.
 */
fun ilerp(x: Float, x0: Float, x1: Float): Float {
    if (x1 == x0) return 0f
    return (x - x0) / (x1 - x0)
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
fun PreviewProximityBarChart(modifier: Modifier = Modifier) {
    val poll = PreviewDataFaker.poll(
        amountOfBallots = 7,
        amountOfProposals = 8,
    )
    val analyzer = ProximityAnalyzer()
    val analysis = analyzer.analyze(
        poll = poll,
    )
    JmTheme {
        Column(modifier) {
            // Text("\uD83E\uDD9D I am the glitch raccoon:")
            PlotTitle("Proximity Bar Chart\n${poll.pollConfig.subject}")
            ProximityBarChart(
                analysis = analysis,
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                ),
            )
        }
    }
}
