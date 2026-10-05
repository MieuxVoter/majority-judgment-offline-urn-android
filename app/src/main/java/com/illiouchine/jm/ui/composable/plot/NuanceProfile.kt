package com.illiouchine.jm.ui.composable.plot

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.extensions.reversedIf
import com.illiouchine.jm.extensions.smartFormat
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.plot.component.AxisLabel
import com.illiouchine.jm.ui.composable.plot.component.PatternedBar
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.plot.utils.favorIntLineCountForBars
import com.illiouchine.jm.ui.preview.PreviewDataFaker.poll
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import io.github.koalaplot.core.animation.StartAnimationUseCase
import io.github.koalaplot.core.animation.StartAnimationUseCase.ExecutionType
import io.github.koalaplot.core.bar.DefaultBarPosition
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.CategoryAxisOffset
import io.github.koalaplot.core.xygraph.LongLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberGridStyle
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import kotlin.math.min

//@Composable
//fun NuanceProfileComposeCharts(
//    modifier: Modifier = Modifier,
//    poll: Poll,
//    moreNuanceToLessNuance: Boolean = false,
//) {
//    val context = LocalContext.current
//    val textColor = Theme.colorScheme.onBackground
//    val maximumNuance = min(
//        a = poll.pollConfig.grading.getAmountOfGrades(),
//        b = poll.pollConfig.proposals.size,
//    )
//    val nuances = remember(poll, poll.ballots.size) {
//        poll.ballots.map { ballot ->
//            // Note: casting to a Set removes duplicates, which is _why_ we do it.
//            ballot.judgments.map { j -> j.grade }.toSet().size
//        }
//    }
//    val barData = remember(poll, poll.ballots.size) {
//        List(size = maximumNuance) { currentNuanceZeroIndexed ->
//            val currentNuance = currentNuanceZeroIndexed + 1
//            Bars(
//                label = currentNuance.toString(),
//                values = listOf(
//                    Bars.Data(
//                        value = nuances.filter { nuance ->
//                            nuance == currentNuance
//                        }.size.toDouble(),
//                        color = SolidColor(textColor),
//                    ),
//                ),
//            )
//        }.reversedIf(moreNuanceToLessNuance)
//    }
//    val dataDescription = remember(poll, poll.ballots.size) {
//        @SuppressLint("LocalContextGetResourceValueCall")
//        buildString {
//            for (currentNuance in (1..maximumNuance).reversedIf(moreNuanceToLessNuance)) {
//                val amountOfBallots = nuances.filter { nuance ->
//                    nuance == currentNuance
//                }.size
//                if (amountOfBallots > 0) {
//                    val comment = if (amountOfBallots > 1) {
//                        if (currentNuance > 1) {
//                            context.getString(
//                                R.string.plot_description_nuance_profile_many_many,
//                                amountOfBallots,
//                                currentNuance,
//                            )
//                        } else {
//                            context.getString(
//                                R.string.plot_description_nuance_profile_many_one,
//                                amountOfBallots,
//                            )
//                        }
//                    } else {
//                        if (currentNuance > 1) {
//                            context.getString(
//                                R.string.plot_description_nuance_profile_one_many,
//                                currentNuance,
//                            )
//                        } else {
//                            context.getString(
//                                R.string.plot_description_nuance_profile_one_one,
//                            )
//                        }
//                    }
//                    append(comment)
//                    append("\n")
//                }
//            }
//        }
//    }
//
//    val horizontalLinesCount = favorIntLineCountForBars(barData)
//
//    ColumnChart(
//        modifier = modifier
//            // The default description by TalkBack on this plot is not relevant.
//            // We craft a better description (see dataDescription above and PlotTitle below)
//            .clearAndSetSemantics {},
//        data = barData,
//        barProperties = BarProperties(
//            thickness = 32.dp,
//            spacing = 0.dp,
//            cornerRadius = Bars.Data.Radius.Rectangle(
//                topLeft = 4.dp,
//                topRight = 4.dp,
//            ),
//        ),
//        labelProperties = LabelProperties(
//            enabled = true,
//            textStyle = TextStyle.Default.copy(
//                fontSize = 10.sp,
//                textAlign = TextAlign.End,
//                color = textColor,
//            ),
//        ),
//        indicatorProperties = HorizontalIndicatorProperties(
//            textStyle = TextStyle.Default.copy(
//                fontSize = 12.sp,
//                textAlign = TextAlign.End,
//                color = textColor,
//            ),
//            contentBuilder = {
//                it.smartFormat()
//            },
//            count = IndicatorCount.CountBased(horizontalLinesCount),
//        ),
//        dividerProperties = DividerProperties(
//            enabled = false,
//        ),
//        gridProperties = GridProperties(
//            enabled = true,
//            xAxisProperties = GridProperties.AxisProperties(
//                enabled = true,
//                lineCount = horizontalLinesCount,
//            ),
//            yAxisProperties = GridProperties.AxisProperties(
//                enabled = false,
//            ),
//        ),
//        // This appears to be glitchy (color dot top left?), let's hide it altogether.
//        labelHelperProperties = LabelHelperProperties(
//            enabled = false,
//        ),
//    )
//
//    PlotTitle(
//        modifier = Modifier
//            .padding(top = Theme.spacing.tiny)
//            .semantics {
//                if (poll.ballots.isNotEmpty()) {
//                    contentDescription = dataDescription
//                }
//            },
//        text = stringResource(R.string.plot_title_nuance_profile),
//    )
//}

@Composable
fun NuanceProfile(
    modifier: Modifier = Modifier,
    // TBD: Replace this var by an intermediary data class like NuanceProfileData
    poll: Poll,
    moreNuanceToLessNuance: Boolean = false,
    animated: Boolean = true,
) {
    val context = LocalContext.current
    val maximumNuance = min(
        a = poll.pollConfig.grading.getAmountOfGrades(),
        b = poll.pollConfig.proposals.size,
    )
    val nuances = remember(
        key1 = poll,
        key2 = poll.ballots.size,
    ) {
        val nuanceByBallot = poll.ballots.map { ballot ->
            // Note: casting to a Set removes duplicates, which is _why_ we do it.
            ballot.judgments.map { j -> j.grade }.toSet().size
        }
        List(maximumNuance) { nuanceIndex ->
            nuanceByBallot.filter { nuance ->
                nuance == (nuanceIndex + 1)
            }.size
        }
    }
    val barData = remember(
        key1 = poll,
        key2 = poll.ballots.size,
    ) {
        List(size = maximumNuance) { nuanceIndex ->
            DefaultVerticalBarPlotEntry(
                x = (nuanceIndex + 1).toString(),
                y = DefaultBarPosition(
                    start = 0L,
                    end = nuances[nuanceIndex].toLong(),
                ),
            )
        }
    }

    // Extensive data description for TalkBack, later applied on the plot's title
    val dataDescription = remember(
        key1 = poll,
        key2 = poll.ballots.size,
    ) {
        @SuppressLint("LocalContextGetResourceValueCall")
        buildString {
            for (currentNuance in (1..maximumNuance).reversedIf(moreNuanceToLessNuance)) {
                val amountOfBallots = nuances[currentNuance - 1]
                if (amountOfBallots > 0) {
                    val comment = if (amountOfBallots > 1) {
                        if (currentNuance > 1) {
                            context.getString(
                                R.string.plot_description_nuance_profile_many_many,
                                amountOfBallots,
                                currentNuance,
                            )
                        } else {
                            context.getString(
                                R.string.plot_description_nuance_profile_many_one,
                                amountOfBallots,
                            )
                        }
                    } else {
                        if (currentNuance > 1) {
                            context.getString(
                                R.string.plot_description_nuance_profile_one_many,
                                currentNuance,
                            )
                        } else {
                            context.getString(
                                R.string.plot_description_nuance_profile_one_one,
                            )
                        }
                    }
                    append(comment)
                    append("\n")
                }
            }
        }
    }

    Column(
        modifier = modifier,
    ) {
        XYGraph(
            xAxisModel = CategoryAxisModel(
                categories = List(size = maximumNuance) { (it + 1).toString() }
                    .reversedIf(moreNuanceToLessNuance),
                categoryAxisOffset = CategoryAxisOffset.Half,
            ),

            yAxisModel = LongLinearAxisModel(
                range = 0L..nuances.max(),
            ),
            xAxisContent = AxisContent(
                labels = {
                    AxisLabel(
                        label = it,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                },
                title = {
                    val plotTitle = stringResource(R.string.plot_title_nuance_profile)
                    PlotTitle(
                        modifier = Modifier
                            .padding(top = Theme.spacing.medium)
                            .semantics {
                                contentDescription = buildString {
                                    append(plotTitle)
                                    append("\n")
                                    append(dataDescription)
                                }
                            },
                        text = plotTitle,
                    )
                },
                style = rememberAxisStyle(
                    majorTickSize = 0.dp,
                    labelRotation = 0,
                ),
            ),
            yAxisContent = AxisContent(
                labels = {
                    AxisLabel(
                        label = "$it",
                        modifier = Modifier.absolutePadding(right = 2.dp),
                    )
                },
                title = {},
                style = rememberAxisStyle(minorTickSize = 0.dp),
            ),
            gridStyle = rememberGridStyle(verticalMajorStyle = null),
        ) {
            VerticalBarPlot(
                data = barData,
                barWidth = 0.42f,
                bar = { barIndex, _, _ ->
                    val nuanceIndex = if (moreNuanceToLessNuance) {
                        maximumNuance - 1 - barIndex
                    } else {
                        barIndex
                    }
                    PatternedBar(
                        color = lerp(
                            start = Theme.colorScheme.onBackground,
                            stop = Color.Gray,
                            fraction = 0.15f,
                        ),
                        label = nuances[nuanceIndex].toString(),
                        labelColor = Theme.colorScheme.background,
                        shape = RoundedCornerShape(
                            topStart = 8f,
                            topEnd = 8f,
                            bottomStart = 0f,
                            bottomEnd = 0f,
                        ),
                    )
                },
                startAnimationUseCase = StartAnimationUseCase(
                    executionType = if (animated) {
                        ExecutionType.Default
                    } else {
                        ExecutionType.None
                    },
                    KoalaPlotTheme.animationSpec,
                )
            )
        }
    }
}

@Preview(
    name = "Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Composable
fun NuanceProfilePreview() {
    val poll = poll(
        grading = Grading.Quality5Grading,
        amountOfProposals = 4,
        amountOfBallots = 300,
    )

    JmTheme {
        Column() {

            NuanceProfile(
                modifier = Modifier
                    .padding(all = Theme.spacing.large)
                    .height(300.dp),
                poll = poll,
                moreNuanceToLessNuance = true,
                animated = false,
            )

            NuanceProfile(
                modifier = Modifier
                    .padding(all = Theme.spacing.large)
                    .height(300.dp),
                poll = poll,
                moreNuanceToLessNuance = false,
                animated = false,
            )
        }
    }
}
