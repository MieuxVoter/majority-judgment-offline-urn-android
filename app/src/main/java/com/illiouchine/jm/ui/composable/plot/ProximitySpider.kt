package com.illiouchine.jm.ui.composable.plot

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.service.ProximityAnalysis
import com.illiouchine.jm.service.ProximityAnalyzer
import com.illiouchine.jm.ui.composable.plot.lib.SpiderChart
import com.illiouchine.jm.ui.composable.plot.utils.makeProposalsInitials
import com.illiouchine.jm.ui.preview.PreviewDataFaker
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme

// Shows how close (in the collective hearts of the judges) pairs of proposals are.
// A proximity of +1 means that the two proposals received exactly the same grades in each ballot.
// A proximity of 0 is indistinguishable from random.
// A proximity of -1 means that the two proposals received extreme and diametrically opposite grades in each ballot.
// Basically:    proximity = (squaredDeviation / maximumDeviation - 0.5) * 2.0
// This assumes that grades are somewhat linearly distributed, value-wise.
@Composable
fun ProximitySpider(
    modifier: Modifier = Modifier,
    analysis: ProximityAnalysis,
    onProposalSelected: (Int) -> Unit = {},
    selectedProposalIndex: Int = 0,
) {
    val proposalsInitials = makeProposalsInitials(analysis)

    SpiderChart(
        modifier = modifier,
        title = {
            Text(
                stringResource(
                    R.string.plot_title_proximity_with,
                    analysis.proposals[selectedProposalIndex]
                )
            )
        },
        // We are not using the legend because it yields a (min > max) error from the Koala lib.
        // legend = {},
        // Truncating is not enough on small screens with big fonts — responsive for tablets ?
        // categories = filteredAnalysis.proposals.map { it.truncate(16, "…") },
        categories = proposalsInitials, // so we use initials, it worked nicely so far
        values = analysis.proximities[selectedProposalIndex].map { it.toFloat() },
        tickValues = listOf(-1f, 0f, 1f),
        tickDecimals = 0,
        highlightedCategoryIndex = selectedProposalIndex,
        onCategoryClick = onProposalSelected,
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Column {
                analysis.proposals.forEachIndexed { index, name ->
                    Row {
                        Text(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        onProposalSelected(index)
                                    }
                                ),
                            style = if (selectedProposalIndex == index) {
                                TextStyle(
                                    color = Theme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                TextStyle()
                            },
                            text = "${proposalsInitials[index]}. $name",
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Preview(
    name = "Phone (Portrait, Big Font)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 2.0f,
)
@Preview(
    name = "Phone (Landscape)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
    fontScale = 1.0f,
)
@Composable
fun PreviewProximitySpider(modifier: Modifier = Modifier) {
    val poll = PreviewDataFaker.poll(
        amountOfBallots = 7,
        amountOfProposals = 20,
    )
    val analyzer = ProximityAnalyzer()
    val analysis = analyzer.analyze(
        poll = poll,
    )
    JmTheme {
        Column(modifier) {
            Text("\uD83E\uDD9D I am the glitch raccoon that looks for glitches:")
            ProximitySpider(
                modifier = Modifier.height(400.dp),
                analysis = analysis,
            )
        }
    }
}

// This is commented out 'til we figure out how to use a CSV file that is excluded from the release.
// The CSV file is in app/src/androidTest/fixtures/ for now ; move it around as needed.
// Also, we need to handle the dataframe business (CSV reader) for this to work.
/*
@Preview(
    name = "2007 Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Preview(
    name = "2007 Phone (Landscape)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
    fontScale = 1.0f,
)
@Preview(
    name = "2007 Square",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=500dp,height=450dp",
)
@Composable
fun PreviewCsvProximitySpider(modifier: Modifier = Modifier) {
    // How to use the test asset CSV file instead ?  (and shake it from release)
    val df = DataFrame.readCsv(LocalContext.current.getRawInput(R.raw.orsay2007))

    val poll = Poll(
        id = 2007,
        pollConfig = PollConfig(
            subject = "2007",
            proposals = df.columnNames(),
            grading = Grading.Quality7Grading,
        ),
        ballots = df.map {
            Ballot(
                judgments = it.values().mapIndexed { index, value ->
                    Judgment(
                        proposal = index,
                        grade = value.toString().toInt(),
                    )
                }.toPersistentList()
            )
        },
    )

    val analyzer = ProximityAnalyzer()
    val analysis = analyzer.analyze(
        poll = poll,
    )

    JmTheme {
        Column(modifier) {
            Text("")
            ProximitySpider(
                modifier = Modifier.height(400.dp),
                analysis = analysis,
            )
        }
    }
}

fun Context.getRawInput(@RawRes resourceId: Int): InputStream {
    return resources.openRawResource(resourceId)
}
*/
