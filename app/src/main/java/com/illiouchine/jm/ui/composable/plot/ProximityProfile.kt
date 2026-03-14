package com.illiouchine.jm.ui.composable.plot

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
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
import com.illiouchine.jm.extensions.smartFormat
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.plot.utils.truncate
import com.illiouchine.jm.ui.preview.PreviewDataFaker
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
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

// Shows how close (in the collective hearts of the judges) pairs of proposals are.
// A proximity of 1 means that the two proposals received exactly the same grades in each ballot.
// A proximity of 0 means that the two proposals received extreme and diametrically opposite grades in each ballot.
// Basically:    proximity = 1.0 - standardDeviation / maximumStandardDeviation
// This assumes that grades are somewhat linearly distributed, value-wise.
@Composable
fun ProximityProfile(
    modifier: Modifier = Modifier,
    poll: Poll,
    animated: Boolean = true,
    onlyProposalsIndices: List<Int>? = null,
) {
    val density = LocalDensity.current
    val textColor = Theme.colorScheme.onBackground
    val primaryColor = Theme.colorScheme.primary

    // What we'd truly want would be the chartWidth inside ColumnChart — see 64.dp hack below
    // But for that we'd need to open up the lib we're using and tweak its internals?
    var chartSizeInPx by remember { mutableStateOf(IntSize.Zero) }
    // Hack: 64.dp is (a little) more than the size of the Y-axis ticks' labels.
    val chartWidth = with(density) {
        chartSizeInPx.width.toDp() - 64.dp
    }

    var maxAmountOfProposals = with(density) {
        // in girum imus nocte | et consumimur igni //
        floor(sqrt(chartWidth.toPx() / 8.dp.toPx())).toInt()
    }

    val allProposalsIndices = 0.rangeUntil(poll.pollConfig.proposals.size).toList()
    val lotsOfProposalsIndices = onlyProposalsIndices ?: allProposalsIndices

    maxAmountOfProposals = min(maxAmountOfProposals, lotsOfProposalsIndices.size)

    val proposalsIndices = lotsOfProposalsIndices.slice(
        0.rangeUntil(maxAmountOfProposals)
    )

    val barWidth = with(density) {
        val n = proposalsIndices.size
        (chartWidth / (n * (2 * n - 1)))
    }

    val barData = remember(poll, poll.ballots.size, proposalsIndices.size) {
        // We need the maximum standard deviation possible in order to normalize
        val maxDifference = poll.pollConfig.grading.getAmountOfGrades() - 1
        val maxDeviation = sqrt((maxDifference * maxDifference * poll.ballots.size).toDouble())

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
                    1.0 - stdDeviation / maxDeviation
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
    val horizontalLinesCount = 5

    ColumnChart(
        modifier = modifier.onGloballyPositioned { coordinates ->
            @Suppress("AssignedValueIsNeverRead") // it IS
            chartSizeInPx = coordinates.size
        },
        data = barData,
        barProperties = BarProperties(
//            thickness = 4.dp,
            thickness = barWidth,
            spacing = 1.dp,

            cornerRadius = Bars.Data.Radius.Rectangle(
                topLeft = 2.dp,
                topRight = 2.dp,
            ),
        ),
        labelProperties = LabelProperties(
            enabled = true,
            padding = Theme.spacing.extraSmall,
            textStyle = TextStyle.Default.copy(
                fontSize = 11.sp,
                textAlign = TextAlign.End,
                color = textColor,
            ),
            // Wip: We can customize the label Composable but … it's delicate, fastidious and will require a TextMeasurer
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
        indicatorProperties = HorizontalIndicatorProperties(
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
fun PreviewProximityProfile(modifier: Modifier = Modifier) {
    val poll = PreviewDataFaker.poll(
        amountOfBallots = 7,
        amountOfProposals = 10,
    )
    JmTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(modifier) {
                Text("\uD83E\uDD9D I am the glitch raccoon that looks for glitches:")
                PlotTitle("Proximity Profile\n${poll.pollConfig.subject}")
                ProximityProfile(
                    modifier = Modifier.height(200.dp),
                    poll = poll,
                    animated = false,
                )
                // Uncomment this to expose trouble with our noob remember {} setup
//                Text("And now the version with a coerced amount of proposals (6):")
//                ProximityProfile(
//                    modifier = Modifier.height(300.dp),
//                    poll = poll,
//                    animated = false,
//                    onlyProposalsIndices = 0.rangeUntil(6).toList(),
//                )
            }
        }
    }
}