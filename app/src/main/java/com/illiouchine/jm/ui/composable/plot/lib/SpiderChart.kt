package com.illiouchine.jm.ui.composable.plot.lib

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.composable.plot.component.PlotTitle
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.polar.PolarGraph
import io.github.koalaplot.core.polar.PolarGraphDefaults
import io.github.koalaplot.core.polar.PolarPlotSeries2
import io.github.koalaplot.core.polar.PolarPoint
import io.github.koalaplot.core.polar.RadialGridType
import io.github.koalaplot.core.polar.rememberCategoryAngularAxisModel
import io.github.koalaplot.core.polar.rememberFloatRadialAxisModel
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi


//data class StarValue(
//    val value: Double,
//)
//data class Star(
//    val values: List<StarValue>,
//)


@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun SpiderChart(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    categories: List<String>,
    values: List<Float>,
    highlightedCategoryIndex: Int = -1,
    onCategoryClick: (categoryIndex: Int) -> Unit = {},
//    stars: List<Star>,
) {
    assert(categories.size == values.size)

    val indexedCategories = categories.mapIndexed { index, name ->
        Pair(index, name)
    }

    // Hmmm ; let's not home-make this just now
//    Canvas(modifier = modifier) {
//        val canvasSize = size
//        val center = PointF(
//            canvasSize.width * 0.5f,
//            canvasSize.height * 0.5f,
//        )
//        val path = Path()
//        path.moveTo(center.x, center.y)
//        path.lineTo(10.0f ,10.0f)
//        path.lineTo(200.0f ,100.0f)
//        path.close()
//        drawPath(
//            path = path,
//            color = Color(1.0f, 0.0f, 0.0f, 1.0f),
//            style = Stroke(
//                width = 4.dp.toPx(),
//                join = StrokeJoin.Bevel,
//            ),
//        )
//    }


    // random data
//    val seriesNames = listOf("Series 1", "Series 2", "Series 3")
//    val data: List<List<PolarPoint<Float, String>>> = buildList {
//        seriesNames.forEach { _ ->
//            add(
//                buildList {
//                    categories.forEach { category ->
//                        add(DefaultPolarPoint(Random.nextDouble(0.0, 1.0).toFloat(), category))
//                    }
//                },
//            )
//        }
//    }
//    val palette = generateHueColorPalette(seriesNames.size)


    ChartLayout(
        modifier = modifier,
        title = title,
//        legend = { Legend(thumbnail) },
//        legendLocation = LegendLocation.BOTTOM,
    ) {
        val angularAxisGridLineStyle = LineStyle(
            brush = SolidColor(value = Theme.colorScheme.onBackground),
            strokeWidth = 1.dp,
        )

        PolarGraph(
            modifier = Modifier.fillMaxWidth(),
            radialAxisModel = rememberFloatRadialAxisModel((0..2).toList().map { it.toFloat() - 1.0f }),
            angularAxisModel = rememberCategoryAngularAxisModel(indexedCategories),
            radialAxisLabels = {
                Text(
                    text = it.toString(),
                    style = TextStyle.Default,
                )
            },
            angularAxisLabels = {
                var style = TextStyle.Default
                if (it.first == highlightedCategoryIndex) {
                    style = style.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Theme.colorScheme.primary,
                    )
                }
                Text(
                    text = it.second,
                    style = style,
                    modifier = Modifier.clickable(
//                        indication = TODO(),
                        onClickLabel = "Center the plot on " + it.second,
//                        role = TODO(),
                        onClick = {
                            onCategoryClick(it.first)
                        }
                    ),
                )
            },
            polarGraphProperties = PolarGraphDefaults
                .polarGraphPropertyDefaults()
                .copy(
                    radialGridType = RadialGridType.LINES,
                    angularAxisGridLineStyle = angularAxisGridLineStyle,
                    radialAxisGridLineStyle = angularAxisGridLineStyle,
                ),
        ) {
            PolarPlotSeries2(
                data = values.mapIndexed { index, value ->
                    PolarPoint(value, indexedCategories[index])
                },
                lineStyle = LineStyle(SolidColor(Theme.colorScheme.primary), strokeWidth = 1.5.dp),
                areaStyle = AreaStyle(SolidColor(Theme.colorScheme.primary), alpha = 0.3f),
                symbols = {
                    Symbol(shape = CircleShape, fillBrush = SolidColor(Theme.colorScheme.primary))
                },
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
fun PreviewSpiderChart(modifier: Modifier = Modifier) {
    JmTheme {
        SpiderChart(
            modifier = modifier.height(400.dp).fillMaxWidth(),
            title = {
                PlotTitle(text = "Proximity of the proposals to François Piquemal")
            },
            highlightedCategoryIndex = 0,
            categories = listOf(
                "François\nPiquemal",
                "Jean-Luc\nMoudenc",
                "François\nBriançon",
                "Yet Another\nOld Male",
                "Arthur\nCottrel",
                "Julien\nLeonardelli",
            ),
            values = listOf(
                1.0f,
                -0.4f,
                0.5f,
                0.0f,
                -0.75f,
                -0.6f,
            ),
        )
    }
}


@Preview(
    name = "Dynamic Phone (Portrait)",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
@Composable
fun PreviewDynamicSpiderChart(modifier: Modifier = Modifier) {
    var selectedCategoryIndex: Int by remember { mutableIntStateOf(0) }
    val valuesPerCategory = listOf(
        // FAC-SIMILE (FOR TESTING)
        listOf(
            1.0f,
            -0.2f,
            0.5f,
            -0.75f,
            -0.6f,
        ),
        listOf(
            -0.2f,
            1.0f,
            0.5f,
            -0.75f,
            -0.6f,
        ),
        listOf(
            0.35f,
            0.0f,
            1.0f,
            -0.75f,
            -0.6f,
        ),
        listOf(
            -0.2f,
            0.5f,
            -0.75f,
            1.0f,
            -0.6f,
        ),
        listOf(
            -0.2f,
            0.5f,
            -0.75f,
            0.6f,
            1.0f,
        ),
    )
    JmTheme {
        SpiderChart(
            modifier = modifier.height(400.dp).fillMaxWidth(),
            title = {
                PlotTitle(text = "Proximity of the proposals to François Piquemal")
            },
            highlightedCategoryIndex = selectedCategoryIndex,
            categories = listOf(
                "François\nPiquemal",
                "Jean-Luc\nMoudenc",
                "François\nBriançon",
                "Arthur\nCottrel",
                "Julien\nLeonardelli",
            ),
            values = valuesPerCategory[selectedCategoryIndex],
            onCategoryClick = { clickedCategoryIndex ->
                selectedCategoryIndex = clickedCategoryIndex
            },
        )
    }
}