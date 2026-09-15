package com.illiouchine.jm.ui.composable.plot

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.extensions.reversedIf
import com.illiouchine.jm.extensions.smartFormat
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.Tally
import com.illiouchine.jm.model.toTally
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.plot.utils.favorIntLineCountForBars
import com.illiouchine.jm.ui.preview.PreviewDataFaker
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import fr.mieuxvoter.mj.CollectedTally
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

/**
 * An Opinion Profile shows how many judgments of each grade were cast in the poll,
 * across all candidates.
 *
 * This helps to get a sense of the overall feel of the voters for the whole set of candidates.
 * This is especially useful to poll administrators since they chose the set of candidates.
 *
 * Ideally the bars in this plot should be made of strata, as many as there are proposals.
 * The lib does not seem to allow this, so we might have to do it ourselves.
 */
@Composable
fun OpinionProfileBarChart(
    modifier: Modifier = Modifier,
    poll: Poll,
    tally: Tally,
    highestGradeToLowestGrade: Boolean = false,
) {
    val context = LocalContext.current
    val barData = remember(poll, poll.ballots.size, highestGradeToLowestGrade) {
        // Cumulative (without strata because the chart lib does not support it out of the box)
        poll.pollConfig.grading.grades.mapIndexed { gradeIndex, grade ->
            @SuppressLint("LocalContextGetResourceValueCall") // how else?
            Bars(
                label = context.getString(grade.name),
                values = listOf(
                    Bars.Data(
                        value = tally.proposalsTallies.sumOf { proposalTally ->
                            proposalTally.tally[gradeIndex]
                        }.toDouble(),
                        color = SolidColor(grade.color),
                    ),
                ),
            )
        }.reversedIf(highestGradeToLowestGrade)
    }
    val dataDescription = remember(poll, poll.ballots.size, highestGradeToLowestGrade) {
        buildString {
            poll.pollConfig.grading.grades
                .reversedIf(highestGradeToLowestGrade)
                .forEachIndexed { i, grade ->
                    val gradeIndex = if (highestGradeToLowestGrade) {
                        poll.pollConfig.grading.grades.size - 1 - i
                    } else {
                        i
                    }
                    val value = tally.proposalsTallies.sumOf { proposalTally ->
                        proposalTally.tally[gradeIndex]
                    }
                    append("${value} ")
                    @SuppressLint("LocalContextGetResourceValueCall")
                    append(context.getString(grade.name))
                    append(",\n")
                }
        }
    }

    val horizontalLinesCount = favorIntLineCountForBars(barData)

    Column {
        ColumnChart(
            modifier = modifier
                // We need to fix the compose-chart lib upstream in order to show this.
                // We do want to iterate over the labels, but we need to say the values too.
                // We work around this by computing a textual data description (see PlotTitle below)
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
                    color = Theme.colorScheme.onBackground,
                ),
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = Theme.colorScheme.onBackground,
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
            labelHelperProperties = LabelHelperProperties(
                enabled = false,
            ),
        )

        // Hotfix for bottom padding being too small when x-axis labels are rotated.
        // This must stay a magic value, since it's a hotfix hack and not theme related.
        Spacer(modifier = Modifier.padding(vertical = 26.dp))

        val plotTitle = stringResource(R.string.plot_title_opinion_profile)
        PlotTitle(
            modifier = Modifier.semantics {
                contentDescription = buildString {
                    append(plotTitle)
                    append("\n")
                    append(dataDescription)
                }
            },
            text = plotTitle,
        )
    }
}

@Preview(
    name = "Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Composable
fun OpinionProfileBarChartPreview() {
    val poll = Poll(
        id = 1,
        pollConfig = PreviewDataFaker.pollConfig(
            amountOfProposals = 3,
        ),
        ballots = listOf(
            Ballot(
                judgments = listOf(
                    Judgment(proposal = 0, grade = 0),
                    Judgment(proposal = 1, grade = 1),
                    Judgment(proposal = 2, grade = 2),
                ),
            ),
            Ballot(
                judgments = listOf(
                    Judgment(proposal = 0, grade = 1),
                    Judgment(proposal = 1, grade = 2),
                    Judgment(proposal = 2, grade = 3),
                ),
            ),
            Ballot(
                judgments = listOf(
                    Judgment(proposal = 0, grade = 2),
                    Judgment(proposal = 1, grade = 3),
                    Judgment(proposal = 2, grade = 4),
                ),
            ),
            Ballot(
                judgments = listOf(
                    Judgment(proposal = 0, grade = 0),
                    Judgment(proposal = 1, grade = 0),
                    Judgment(proposal = 2, grade = 0),
                ),
            ),
        ),
    )

    // Refactor the following into a service (but first recode the MJ lib in Kotlin)
    val amountOfProposals = poll.pollConfig.proposals.size
    val amountOfGrades = poll.pollConfig.grading.getAmountOfGrades()
    val tally = CollectedTally(amountOfProposals, amountOfGrades)

    poll.pollConfig.proposals.forEachIndexed { proposalIndex, _ ->
        val voteResult = poll.judgments.filter { it.proposal == proposalIndex }
        voteResult.forEach { judgment ->
            tally.collect(proposalIndex, judgment.grade)
        }
    }
    // ----------------------------------------------------------------------------

    JmTheme {
        OpinionProfileBarChart(
            modifier = Modifier.height(400.dp).padding(8.dp),
            poll = poll,
            tally = tally.toTally(),
        )
    }
}
