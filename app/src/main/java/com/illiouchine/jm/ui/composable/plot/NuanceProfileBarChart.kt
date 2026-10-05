package com.illiouchine.jm.ui.composable.plot

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.extensions.reversedIf
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.plot.component.AxisLabel
import com.illiouchine.jm.ui.composable.plot.component.PatternedBar
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
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
import kotlin.math.min

@Composable
fun NuanceProfileBarChart(
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
                    PatternedBar(
                        color = lerp(
                            start = Theme.colorScheme.onBackground,
                            stop = Color.Gray,
                            fraction = 0.15f,
                        ),
                        label = nuances[barIndex].toString(),
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
fun NuanceProfileBarChartPreview() {
    val poll = poll(
        grading = Grading.Quality5Grading,
        amountOfProposals = 4,
        amountOfBallots = 300,
    )

    JmTheme {
        Column() {

            NuanceProfileBarChart(
                modifier = Modifier
                    .padding(all = Theme.spacing.large)
                    .height(300.dp),
                poll = poll,
                moreNuanceToLessNuance = true,
                animated = false,
            )

            NuanceProfileBarChart(
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
