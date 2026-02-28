package com.illiouchine.jm.ui.composable.plot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.logic.reversedIf
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.Tally
import com.illiouchine.jm.ui.theme.Theme
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

/**
 * An opinion profile shows how many judgments of each grade were cast in the poll,
 * "tous candidats confondus".
 * This helps getting a sense of the overall feel of the judges for the whole set of candidates.
 * This is especially useful to poll administrators in check since they chose the set of candidates.
 * It could be used constitutionally to throw out the whole poll and start it anew with new administrators.
 *
 * Ideally the bars in this plot should be made of strata, as many as there are proposals.
 * The lib does not seem to allow this, so we might have to do it ourselves.
 */
@Composable
fun OpinionProfile(
    modifier: Modifier = Modifier,
    poll: Poll,
    tally: Tally,
    greenToRed: Boolean = true,
) {
    val context = LocalContext.current
    val barData = remember (poll, poll.ballots.size) {
        // Cumulative (without strata because the lib does not support it out of the box)
        poll.pollConfig.grading.grades.mapIndexed { gradeIndex, grade ->
            @SuppressLint("LocalContextGetResourceValueCall") // how else?
            Bars(
                label = context.getString(grade.name),
                values = listOf(
                    Bars.Data(
                        value = tally.proposalsTallies.map { proposalTally ->
                            proposalTally.tally[gradeIndex].toDouble()
                        }.reduce { acc, value -> acc + value },
                        color = SolidColor(grade.color),
                    ),
                ),
            )
        }.reversedIf(greenToRed)
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
                color = Theme.colorScheme.onBackground,
            ),
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = TextStyle.Default.copy(
                fontSize = 10.sp,
                textAlign = TextAlign.End,
                color = Theme.colorScheme.onBackground,
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

    // Hotfix for bottom padding being too small when x-axis labels are rotated.
    // This must stay a magic value, since it's a hotfix hack and not theme related.
    Spacer(modifier = Modifier.padding(vertical = 24.dp))
}
