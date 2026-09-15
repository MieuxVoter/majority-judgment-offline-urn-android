package com.illiouchine.jm.ui.composable.plot

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.extensions.reversedIf
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.Tally
import com.illiouchine.jm.model.toTally
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.composable.spacer.MediumVerticalSpacer
import com.illiouchine.jm.ui.preview.PreviewDataFaker
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import fr.mieuxvoter.mj.CollectedTally
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.DefaultBarPosition
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.CategoryAxisOffset
import io.github.koalaplot.core.xygraph.LongLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberGridStyle


@Composable
fun AxisLabel(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        label,
        color = Theme.colorScheme.onBackground,
        style = Theme.typography.bodySmall,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}

/**
 * An Opinion Profile shows how many judgments of each grade were cast across all candidates.
 *
 * This helps to get a sense of the overall feel of the voters for the whole set of candidates.
 * This is especially useful to poll administrators since they chose the set of candidates.
 */
@Composable
fun OpinionProfileBarChartKoala(
    modifier: Modifier = Modifier,
    tally: Tally,
    grading: Grading,
    highestGradeToLowestGrade: Boolean = false,
    animated: Boolean = true,
) {
    val opinionTally = List(grading.grades.size) { gradeIndex ->
        tally.proposalsTallies.sumOf { proposalTally ->
            proposalTally.tally[gradeIndex]
        }
    }

    val gradesNames = grading.grades.map { stringResource(it.name) }

    val dataDescription = remember(
        tally,
        highestGradeToLowestGrade,
    ) {
        buildString {
            grading.grades
                .reversedIf(highestGradeToLowestGrade)
                .forEachIndexed { i, _ ->
                    val gradeIndex = if (highestGradeToLowestGrade) {
                        grading.grades.size - 1 - i
                    } else {
                        i
                    }
                    append("${opinionTally[gradeIndex]} ${gradesNames[gradeIndex]}")
                    append(",\n")
                }
        }
    }

    val barData = List(grading.grades.size) { gradeIndex ->
        DefaultVerticalBarPlotEntry(
            x = gradesNames[gradeIndex],
            y = DefaultBarPosition(
                start = 0L,
                end = opinionTally[gradeIndex].toLong(),
            ),
        )
    }.reversedIf(highestGradeToLowestGrade)

    val maxValue = opinionTally.max()

    Column {

        XYGraph(
            modifier = modifier,
            xAxisModel = CategoryAxisModel(
                categories = gradesNames.reversedIf(highestGradeToLowestGrade),
                categoryAxisOffset = CategoryAxisOffset.Half,
            ),
            yAxisModel = LongLinearAxisModel(
                range = 0L..maxValue.toInt(), // why do we have to cast to Int here ?
            ),
            xAxisContent = AxisContent(
                labels = {
                    AxisLabel(
                        label = it,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                },
                title = {},
                style = rememberAxisStyle(labelRotation = 42),
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
                    val gradeIndex = if (highestGradeToLowestGrade) {
                        grading.grades.size - 1 - barIndex
                    } else {
                        barIndex
                    }
                    DefaultBar(
                        brush = SolidColor(grading.getGradeColor(gradeIndex)),
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
            )
        }

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
fun OpinionProfileBarChartKoalaPreview() {
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
                    Judgment(proposal = 2, grade = 2),
                ),
            ),
            Ballot(
                judgments = listOf(
                    Judgment(proposal = 0, grade = 2),
                    Judgment(proposal = 1, grade = 0),
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
        Column {
            OpinionProfileBarChartKoala(
                modifier = Modifier.height(300.dp).padding(8.dp),
                tally = tally.toTally(),
                grading = Grading.Quality5Grading,
                animated = false,
            )
            MediumVerticalSpacer()
            MediumVerticalSpacer()
            OpinionProfileBarChartKoala(
                modifier = Modifier.height(300.dp).padding(8.dp),
                tally = tally.toTally().copy(),
                grading = Grading.Quality5Grading,
                highestGradeToLowestGrade = true,
                animated = false,
            )
        }
    }
}
