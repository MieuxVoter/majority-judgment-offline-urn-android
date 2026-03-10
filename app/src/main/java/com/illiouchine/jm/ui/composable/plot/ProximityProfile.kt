package com.illiouchine.jm.ui.composable.plot

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.extensions.smartFormat
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.preview.PreviewDataBuilder
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
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import kotlin.math.sqrt

// Shows how close (in the collective hearts of the judges) pairs of proposals are.
// A proximity of 1 means that the two proposals received exactly the same mentions in each ballot.
// A proximity of 0 means that the two proposals received extreme and diametrically opposite mentions in each ballot.
// Basically:    proximity = 1.0 - standardDeviation / maximumStandardDeviation
@Composable
fun ProximityProfile(
    modifier: Modifier = Modifier,
    poll: Poll,
    animated: Boolean = true,
) {
    val textColor = Theme.colorScheme.onBackground
    val primaryColor = Theme.colorScheme.primary
    val barData = remember(poll, poll.ballots.size) {
        // TBD: we want another proposal order (ranking order?) — Watch out for Ballot.gradeOf() below though
        val proposals = poll.pollConfig.proposals
        // We need the maximum standard deviation possible in order to normalize
        val maxDifference = poll.pollConfig.grading.getAmountOfGrades() - 1
        val maxDeviation = sqrt((maxDifference * maxDifference * poll.ballots.size).toDouble())

        val proximities = proposals.mapIndexed { someProposalIndex, _ ->
            proposals.mapIndexed { otherProposalIndex, _ ->
                if (maxDeviation == 0.0) { // true iff there are no ballots or only one grade
                    if (someProposalIndex == otherProposalIndex) {
                        1.0
                    } else {
                        0.0
                    }
                } else {
                    val stdDeviation = sqrt(
                        poll.ballots.map { ballot ->
                            val someGradeValue = ballot.gradeOf(someProposalIndex)
                            val otherGradeValue = ballot.gradeOf(otherProposalIndex)
                            (someGradeValue - otherGradeValue) * (someGradeValue - otherGradeValue)
                        }.reduce { acc, value -> acc + value }.toDouble()
                    )
                    1.0 - stdDeviation / maxDeviation
                }
            }
        }

        poll.pollConfig.proposals.mapIndexed { proposalIndex, proposal ->
            Bars(
                label = proposal,
                values = proximities[proposalIndex].mapIndexed { otherProposalIndex, proximity ->
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
        modifier = modifier.padding(bottom = Theme.spacing.large),
        data = barData,
        barProperties = BarProperties(
            thickness = 4.dp,
            spacing = 1.dp,
            cornerRadius = Bars.Data.Radius.Rectangle(
                topLeft = 2.dp,
                topRight = 2.dp,
            ),
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                fontSize = 10.sp,
                textAlign = TextAlign.End,
                color = textColor,
            ),
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = TextStyle.Default.copy(
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                color = textColor,
            ),
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
}


@Preview(
    name = "Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Composable
fun PreviewProximityProfile(modifier: Modifier = Modifier) {
    val poll = PreviewDataBuilder.poll(
        amountOfBallots = 7,
    )
    JmTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(modifier) {
                Text("Proximity Profile of ${poll.pollConfig.subject}")
                ProximityProfile(
                    modifier = Modifier.height(300.dp),
                    poll = poll,
                    animated = false,
                )
                PlotTitle("Plot Legend")
                Text("Some other text below, no padding applied.")
            }
        }
    }
}