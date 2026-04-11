package com.illiouchine.jm.ui.composable.plot

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.extensions.shortenNames
import com.illiouchine.jm.extensions.smartFormat
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.service.ProximityAnalysis
import com.illiouchine.jm.service.ProximityAnalyzer
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.plot.utils.truncate
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
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import java.lang.Integer.min
import kotlin.math.floor
import kotlin.math.sqrt

//
// Shows how close (in the collective hearts of the judges) pairs of proposals are.
// A proximity of +1 means that the two proposals received exactly the same grades in each ballot.
// A proximity of -1 means that the two proposals received extreme and diametrically opposite grades in each ballot.
// Basically:    proximity = (squaredDeviation / maximumDeviation - 0.5) * 2.0
// This assumes that grades are somewhat linearly distributed, value-wise.
//
// There are two plots:
// - One made with Compose Charts (the "old" one, kept until it's stale enough)
// - One made with Koala (the "current" one in use)
//

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ProximityBarChart(
    modifier: Modifier = Modifier,
    analysis: ProximityAnalysis,
    proposalsIndices: List<Int>? = null, // show all proposals (up to 16) if not set
) {
    val primaryColor = Theme.colorScheme.primary

    val allProposalsIndices = 0.rangeUntil(analysis.proposals.size).toList()
    val lotsOfProposalsIndices = proposalsIndices ?: allProposalsIndices

    val maxAmountOfProposalsThatFit = 16
    val maxAmountOfProposals = min(maxAmountOfProposalsThatFit, lotsOfProposalsIndices.size)
    val usedProposalsIndices = lotsOfProposalsIndices.take(maxAmountOfProposals).reversed()
    val filteredAnalysis = analysis.filterByProposalsIndices(usedProposalsIndices)

    val proposalsInitials = filteredAnalysis.proposals.shortenNames().map {
        it.truncate(
            maxLength = 7, // check big fonts on small screens if you increment this
            ellipsis = "…",
        )
    }

    XYGraph(
        modifier = modifier,
        xAxisModel = rememberFloatLinearAxisModel(-1f..1f, minorTickCount = 0),
        yAxisModel = remember(analysis, proposalsIndices) {
            CategoryAxisModel(
                categories = usedProposalsIndices.map { proposalsInitials[it] },
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
        val textColor = Theme.colorScheme.onBackground
        GroupedHorizontalBarPlot(
            maxBarGroupWidth = 0.666f,
            startAnimationUseCase = StartAnimationUseCase(
                executionType = StartAnimationUseCase.ExecutionType.Default,
                KoalaPlotTheme.animationSpec,
            )
        ) {
            usedProposalsIndices.forEach { categoryIndex ->
                series(
                    defaultBar = horizontalSolidBar(
                        color = textColor,
                    ),
                ) {
                    usedProposalsIndices.forEach { proposalIndex ->
                        val name = proposalsInitials[proposalIndex]
                        val isOwnBar = (categoryIndex == proposalIndex)
                        item(
                            y = name,
                            xMin = if (isOwnBar) {
                                analysis.minima[categoryIndex].toFloat()
                            } else {
                                0f
                            },
                            xMax = analysis.proximities[categoryIndex][proposalIndex].toFloat(),
                            bar = if (isOwnBar) {
                                horizontalSolidBar(
                                    color = primaryColor,
                                )
                            } else {
                                null // i.e. use the default bar
                            },
                        )
                    }
                }
            }
        }
    }
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
        amountOfProposals = 13,
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

// will be safe to remove soon
@Deprecated("Use ProximityBarChart instead.")
@Composable
fun ProximityBarChartOld(
    modifier: Modifier = Modifier,
    poll: Poll,
    animated: Boolean = true,
    onlyProposalsIndices: List<Int>? = null,
) {
    val density = LocalDensity.current
    val textColor = Theme.colorScheme.onBackground
    val primaryColor = Theme.colorScheme.primary

    // We need the size of the chart to compute how many proposals can fit in it.
    // What we'd truly want would be the chartWidth inside ColumnChart — see ~64.dp hack below
    // But for that we'd need to open up the lib we're using and tweak its internals?
    var chartSizeInPx by remember { mutableStateOf(IntSize.Zero) }
    // Hack: 64.dp is (a big little) more than the size of the Y-axis ticks' labels.
    val chartWidth = with(density) {
        chartSizeInPx.width.toDp()
    }

    val allProposalsIndices = 0.rangeUntil(poll.pollConfig.proposals.size).toList()
    val lotsOfProposalsIndices = onlyProposalsIndices ?: allProposalsIndices

    val maxAmountOfProposalsThatFit = with(density) {
        // in girum imus nocte | et consumimur igni //
        floor(sqrt(chartWidth.toPx() / 7.dp.toPx())).toInt()
    }
    val maxAmountOfProposals = min(maxAmountOfProposalsThatFit, lotsOfProposalsIndices.size)

    val proposalsIndices = lotsOfProposalsIndices.slice(0..<maxAmountOfProposals)

    val barWidth = with(density) {
        val n = proposalsIndices.size
        (chartWidth / (n * (2 * n - 1)))
    }

    val barData = remember(poll, poll.ballots.size, proposalsIndices.size) {
        // We need the maximum standard deviation possible in order to normalize
        val maxDifference = poll.pollConfig.grading.getAmountOfGrades() - 1
        val maxDeviation = sqrt((maxDifference * maxDifference * poll.ballots.size).toDouble())

        // Note: we now have a proximity analyzer class ; use it instead of this
        val proximities = proposalsIndices.map { someProposalIndex ->
            proposalsIndices.map { otherProposalIndex ->
                if (maxDeviation == 0.0) { // true iff there are no ballots or only one grade
                    if (someProposalIndex == otherProposalIndex) {
                        1.0
                    } else {
                        0.0
                    }
                } else {
                    val stdDeviation = sqrt(
                        poll.ballots.sumOf { ballot ->
                            val someGradeValue = ballot.gradeOf(someProposalIndex)
                            val otherGradeValue = ballot.gradeOf(otherProposalIndex)
                            (someGradeValue - otherGradeValue) * (someGradeValue - otherGradeValue)
                        }.toDouble()
                    )
                    ((1.0 - stdDeviation / maxDeviation) - 0.5) * 2.0
                }
            }
        }

        proposalsIndices.mapIndexed { index, proposalIndex ->
            val proposal = poll.pollConfig.proposals[proposalIndex]
            Bars(
                label = proposal.replace("\n", "").truncate(
                    maxLength = 15,
                    ellipsis = "…",
                ),
                values = proposalsIndices.mapIndexed { otherIndex, otherProposalIndex ->
                    val proximity = proximities[index][otherIndex]
                    Bars.Data(
                        value = proximity,
                        color = if (proposalIndex == otherProposalIndex) {
                            SolidColor(primaryColor)
                        } else {
                            SolidColor(textColor)
                        },
                    )
                },
            )
        }
    }
    val horizontalLinesCount = 9

    ColumnChart(
        modifier = modifier.onGloballyPositioned { coordinates ->
            @Suppress("AssignedValueIsNeverRead") // it IS
            chartSizeInPx = coordinates.size
        },
        data = barData,
        barProperties = BarProperties(
            thickness = barWidth,
            spacing = 1.dp,

            cornerRadius = Bars.Data.Radius.Rectangle(
                topLeft = 2.dp,
                topRight = 2.dp,
            ),
        ),
        // X Axis Ticks' Labels
        labelProperties = LabelProperties(
            enabled = true,
            padding = Theme.spacing.extraSmall,
            textStyle = TextStyle.Default.copy(
                fontSize = 11.sp,
                textAlign = TextAlign.End,
                color = textColor,
            ),
            rotation = LabelProperties.Rotation(
                mode = LabelProperties.Rotation.Mode.Force,
            ),
            // Wip: We can customize the label Composable but … it will require a TextMeasurer
            // See https://github.com/ehsannarmani/ComposeCharts/blob/master/compose-charts/src/commonMain/kotlin/ir/ehsannarmani/compose_charts/utils/Labels.kt#L83
//            builder = { modifier, label, shouldRotate, _ ->
//                BasicText(
//                    modifier = modifier.background(color = Color.Magenta),
//                    text = label,
//                    style = TextStyle.Default.copy(
//                        fontSize = 11.sp,
//                        textAlign = if (shouldRotate) TextAlign.End else TextAlign.Center,
//                        color = textColor,
//                    ),
//                    overflow = if (shouldRotate) TextOverflow.Visible else TextOverflow.Clip,
//                    softWrap = !shouldRotate,
//                )
//            },
        ),
        // Y Axis ticks' labels
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = false,
            padding = 20.dp,
            textStyle = TextStyle.Default.copy(
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                color = textColor,
            ),
            position = IndicatorPosition.Horizontal.Start,
            contentBuilder = {
                it.smartFormat()
            },
            count = IndicatorCount.CountBased(horizontalLinesCount),
        ),
        dividerProperties = DividerProperties(
            enabled = false,
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                lineCount = horizontalLinesCount,
            ),
            yAxisProperties = GridProperties.AxisProperties(
                enabled = false,
            ),
        ),
        // This appears to be glitchy (color dot top left?), let's hide it altogether.
        labelHelperProperties = LabelHelperProperties(
            enabled = false,
        ),
        animationMode = if (animated) {
            AnimationMode.Together { it * 240L }
        } else {
            AnimationMode.None
        },
    )
    // Hack: work around limitations of the compose charts lib.
    // Just color the background of the x-axis ticks' labels to see what I mean.
    // With a careful formula and a TextMeasurer perhaps we can replace this.
    Spacer(modifier = Modifier.height(56.dp))
}

@Preview(
    name = "Old Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Preview(
    name = "Old Phone (Landscape)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
    fontScale = 1.0f,
)
@Composable
fun PreviewProximityBarChartOld(modifier: Modifier = Modifier) {
    val poll = PreviewDataFaker.poll(
        amountOfBallots = 7,
        amountOfProposals = 13,
    )
    JmTheme {
        Column(modifier) {
            Text("\uD83E\uDD9D I am the glitch raccoon that looks for glitches:")
            PlotTitle("Proximity Profile\n${poll.pollConfig.subject}")
            @Suppress("DEPRECATION")
            ProximityBarChartOld(
                modifier = Modifier.height(200.dp),
                poll = poll,
                animated = false,
            )
        }
    }
}
