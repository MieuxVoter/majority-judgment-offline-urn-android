package com.illiouchine.jm.ui.composable.plot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.logic.reversedIf
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.theme.Theme
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

@Composable
fun NuanceProfile(
    modifier: Modifier = Modifier,
    poll: Poll,
    lessToMore: Boolean = true,
) {
    val textColor = Theme.colorScheme.onBackground
    val barData = remember (poll, poll.ballots.size) {
        val nuances = poll.ballots.map { ballot ->
            // Note: casting to a Set removes duplicates, which is _why_ we do it.
            ballot.judgments.map { j -> j.grade }.toSet().size
        }
        poll.pollConfig.grading.grades.mapIndexed { gradeIndex, _ ->
            val currentNuance = gradeIndex + 1
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
        }.reversedIf(!lessToMore)
    }

    ColumnChart(
        modifier = modifier,
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
                fontSize = 10.sp,
                textAlign = TextAlign.End,
                color = textColor,
            ),
        ),
        dividerProperties = DividerProperties(
            enabled = false,
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                enabled = true,
            ),
            yAxisProperties = GridProperties.AxisProperties(
                enabled = false,
            ),
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = false,
        ),
    )
}
