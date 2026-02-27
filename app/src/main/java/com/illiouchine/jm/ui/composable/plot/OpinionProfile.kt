package com.illiouchine.jm.ui.composable.plot

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.logic.reversedIf
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.Tally
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.Line

//import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
//import org.jetbrains.kotlinx.dataframe.api.gather
//import org.jetbrains.kotlinx.dataframe.api.groupBy
//import org.jetbrains.kotlinx.dataframe.api.into
//import org.jetbrains.kotlinx.kandy.dsl.categorical
//import org.jetbrains.kotlinx.kandy.dsl.plot
//import org.jetbrains.kotlinx.kandy.letsplot.export.toPNG
//import org.jetbrains.kotlinx.kandy.letsplot.feature.Position
//import org.jetbrains.kotlinx.kandy.letsplot.feature.position
//import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
//import org.jetbrains.kotlinx.kandy.letsplot.translator.toLetsPlot
//import org.jetbrains.kotlinx.kandy.util.color.Color as KandyColor

@Composable
fun OpinionProfile(
    modifier: Modifier = Modifier,
    poll: Poll,
    tally: Tally,
    greenToRed: Boolean = true,
) {

    val data = remember {
        // One line per proposal, somewhat redundant with merit profiles, uninteresting
//            tally.proposalsTallies.mapIndexed { i, proposalTally ->
//                Line(
//                    label = poll.pollConfig.proposals[i].toString(),
//                    values = proposalTally.tally.map { gradeTally -> gradeTally.toDouble() },
//                    color = SolidColor(Color(0xFF23af92)),
//                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
//                    secondGradientFillColor = Color.Transparent,
//                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
//                    gradientAnimationDelay = 2000,
//                    drawStyle = DrawStyle.Stroke(width = 2.dp),
//                )
//            }

        // Cumulative (without strata because the lib does not support it out of the box)
        listOf(
            Line(
                values = poll.pollConfig.grading.grades.mapIndexed { gradeIndex, _ ->
                    tally.proposalsTallies.map { proposalTally ->
                        proposalTally.tally[gradeIndex].toDouble()
                    }.reduce { acc, value -> acc + value }
                }.reversedIf(greenToRed),
                color = SolidColor(Color(0xFF23af92)),
                firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(3000, easing = EaseInOutCubic),
                gradientAnimationDelay = 2500,
                drawStyle = DrawStyle.Stroke(width = 2.dp),
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color.White),
                    strokeWidth = 4.dp,
                    radius = 7.dp,
                    strokeColor = SolidColor(Color(0xFF23af92)),
                ),
            ),
        )
    }

    LineChart(
        modifier = modifier,
        data = data,
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                lineCount = poll.pollConfig.grading.getAmountOfGrades() + 1,
            ),
        ),
        animationMode = AnimationMode.Together(delayBuilder = {
            it * 618L
        }),
    )
}

//@Composable
//fun OpinionProfileKandy(
//    modifier: Modifier = Modifier,
//    tally: Tally,
//    proposalResult: ProposalResult,
//    grading: Grading,
//    decisiveGroups: ImmutableList<ParticipantGroupAnalysis>,
//    showDecisiveGroups: Boolean = false,
//) {
//    val dataset = dataFrameOf(
//        "day" to listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
//        "coffee" to listOf(0.81, 0.78, 0.72, 0.65, 0.73, 0.49, 0.38),
//        "tea" to listOf(0.12, 0.16, 0.21, 0.26, 0.24, 0.22, 0.30),
//        "soda" to listOf(0.07, 0.06, 0.07, 0.09, 0.03, 0.29, 0.32),
//    ).gather("coffee", "tea", "soda").into("drink", "amount")
//
//    val plot = dataset.groupBy("drink").plot {
//        //layout.title = "Weekly Beverage Consumption Trends"
//        bars {
//            x("day")
//            y("amount")
//            fillColor("drink") {
//                scale = categorical(
//                    "coffee" to KandyColor.hex("#6F4E37"),
//                    "tea" to KandyColor.hex("#C2D4AB"),
//                    "soda" to KandyColor.hex("#B5651D")
//                )
//            }
//            position = Position.stack()
//        }
//    }
//    plot.toPNG()
//}
