package com.illiouchine.jm.ui.composable.plot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.extensions.reversedIf
import com.illiouchine.jm.extensions.smartFormat
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.plot.utils.favorIntLineCountForBars
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
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

@Composable
fun NuanceProfile(
    modifier: Modifier = Modifier,
    poll: Poll,
    moreNuanceToLessNuance: Boolean = false,
) {
    val context = LocalContext.current
    val textColor = Theme.colorScheme.onBackground
    val maximumNuance = min(
        poll.pollConfig.grading.getAmountOfGrades(),
        poll.pollConfig.proposals.size,
    )
    val nuances = remember(poll, poll.ballots.size) {
        poll.ballots.map { ballot ->
            // Note: casting to a Set removes duplicates, which is _why_ we do it.
            ballot.judgments.map { j -> j.grade }.toSet().size
        }
    }
    val barData = remember(poll, poll.ballots.size) {
        List(size = maximumNuance) { currentNuanceZeroIndexed ->
            val currentNuance = currentNuanceZeroIndexed + 1
            Bars(
                label = currentNuance.toString(),
                values = listOf(
                    Bars.Data(
                        value = nuances.filter { nuance ->
                            nuance == currentNuance
                        }.size.toDouble(),
                        color = SolidColor(textColor),
                    ),
                ),
            )
        }.reversedIf(moreNuanceToLessNuance)
    }
    val dataDescription = remember(poll, poll.ballots.size) {
        @SuppressLint("LocalContextGetResourceValueCall")
        buildString {
            for (currentNuance in (1..maximumNuance).reversedIf(moreNuanceToLessNuance)) {
                val amountOfBallots = nuances.filter { nuance ->
                    nuance == currentNuance
                }.size
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

    val horizontalLinesCount = favorIntLineCountForBars(barData)

    ColumnChart(
        modifier = modifier
            // The default description by TalkBack on this plot is not relevant.
            // We craft a better description (see dataDescription above and PlotTitle below)
            .clearAndSetSemantics {},
        data = barData,
        barProperties = BarProperties(
            thickness = 32.dp,
            spacing = 0.dp,
            cornerRadius = Bars.Data.Radius.Rectangle(
                topLeft = 4.dp,
                topRight = 4.dp,
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
    )

    PlotTitle(
        modifier = Modifier
            .padding(top = Theme.spacing.tiny)
            .semantics {
                if (poll.ballots.isNotEmpty()) {
                    contentDescription = dataDescription
                }
            },
        text = stringResource(R.string.plot_title_nuance_profile),
    )
}
