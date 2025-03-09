package com.illiouchine.jm.ui.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.illiouchine.jm.model.Grading
import fr.mieuxvoter.mj.ProposalResultInterface
import fr.mieuxvoter.mj.TallyInterface
import java.util.Locale
import kotlin.math.round

@Composable
fun LinearMeritProfileCanvas(
    modifier: Modifier = Modifier,
    tally: TallyInterface,
    proposalResult: ProposalResultInterface,
    grading: Grading,
) {

    val textMeasurer = rememberTextMeasurer()
    val contrastedColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val widthAnimation = remember { Animatable(0f) }
    val outlineAlphaAnimation = remember { Animatable(0f) }
    val outlineAnimation = remember { Animatable(0f) }
    val percentageAnimation = remember { Animatable(0f) }

    LaunchedEffect("apparition") {
        widthAnimation.animateTo(1f, tween(1500, 1000))
        outlineAlphaAnimation.animateTo(1f, tween(600, 150))
        outlineAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 400,
                easing = { x -> x * x * x },
            )
        )
        percentageAnimation.animateTo(1f, tween(3000, 150))
    }

    // Draw the linear merit profile of the proposal.
    Canvas(
        modifier = modifier,
    ) {
        val proposalTally = tally.proposalsTallies[proposalResult.index]
        val middleX = size.width * 0.5f
        var offsetX = 0f
        val medianGradeOutline = Path()
        val balancedGradeWidth = size.width / grading.getAmountOfGrades()

        for (gradeIndex in (0..<grading.getAmountOfGrades()).reversed()) {
            var gradeWidth = (size.width * proposalTally.tally[gradeIndex].toFloat()) /
                    proposalTally.amountOfJudgments.toFloat()
            gradeWidth = lerp(balancedGradeWidth, gradeWidth, widthAnimation.value)
            val gradeRectSize = Size(gradeWidth, size.height)
            val gradeRectOffset = Offset(offsetX, 0f)

            // Fill a rectangle with the color of the grade
            drawRect(
                color = grading.getGradeColor(gradeIndex),
                size = gradeRectSize,
                topLeft = gradeRectOffset,
            )

            // Show the percentage under each grade with at least one judgment
            if (gradeWidth > 0f) {
                val percentage = 100f * gradeWidth / size.width
                val approximate = if (round(percentage) != percentage) {
                    "~"
                } else {
                    ""
                }
                val measuredText =
                    textMeasurer.measure(
                        text = AnnotatedString(
                            String.format(
                                locale = Locale.FRANCE,
                                format = "$approximate%.0f%%",
                                percentage,
                            )
                        ),
                        style = TextStyle(fontSize = 10.sp),
                    )
                drawText(
                    textLayoutResult = measuredText,
                    topLeft = gradeRectOffset + Offset(
                        x = (gradeRectSize.width - measuredText.size.width) * 0.5f,
                        y = size.height + 4.dp.toPx(),
                    ),
                    color = contrastedColor,
                    alpha = 0.78f * percentageAnimation.value,
                )
            }

            // Outline only the median grade
            if (gradeIndex == proposalResult.analysis.medianGrade) {
                val medianGradeRectInitialWidth = 2.dp.toPx()
                medianGradeOutline.addRect(
                    Rect(
                        size = Size(
                            width = lerp(
                                start = medianGradeRectInitialWidth,
                                stop = gradeRectSize.width,
                                fraction = outlineAnimation.value,
                            ),
                            height = gradeRectSize.height,
                        ),
                        offset = Offset(
                            x = lerp(
                                middleX - medianGradeRectInitialWidth * 0.5f,
                                gradeRectOffset.x,
                                fraction = outlineAnimation.value,
                            ),
                            y = gradeRectOffset.y,
                        ),
                    )
                )
            }

            offsetX += gradeWidth
        }


        // Draw the median grade outline *after* drawing all the grade rectangles
        drawPath(
            color = contrastedColor,
            path = medianGradeOutline,
            style = Stroke(
                width = 3.dp.toPx(),
                join = StrokeJoin.Bevel,
            ),
            alpha = outlineAlphaAnimation.value,
        )

        // Amount by which the median line overshoots the merit profile vertically
        val medianLineVerticalOvershoot = 3.dp.toPx()

        // Vertical line in the middle, marking the median grade.
        drawLine(
            color = contrastedColor,
            start = Offset(middleX, 0 - medianLineVerticalOvershoot),
            end = Offset(middleX, size.height + medianLineVerticalOvershoot),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(3.dp.toPx(), 1.dp.toPx()),
                phase = -1.dp.toPx(),
            ),
            strokeWidth = 1.dp.toPx(),
            alpha = widthAnimation.value,
        )
    }
}