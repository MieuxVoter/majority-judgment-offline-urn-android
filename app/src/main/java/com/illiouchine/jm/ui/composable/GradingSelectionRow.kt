package com.illiouchine.jm.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.gradings
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun GradingSelectionRow(
    modifier: Modifier = Modifier,
    grading: Grading = DEFAULT_GRADING_QUALITY_VALUE,
    onGradingSelected: (Grading) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        var expanded by remember { mutableStateOf(false) }

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.label_grades))
                TextButton(
                    onClick = { expanded = !expanded },
                ) {
                    Text(stringResource(grading.name))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "drop down arrow",
                    )
                }
            }

            AnimatedVisibility(expanded) {

                val lazyListState = rememberLazyListState()
                LaunchedEffect(expanded) {
                    lazyListState.animateScrollToItem(index = gradings.indexOf(grading))
                }

                LazyRow(
                    state = lazyListState,
                ) {
                    items(gradings.size) {
                        var thumbnailModifier: Modifier = Modifier
                        if (grading == gradings[it]) {
                            thumbnailModifier = thumbnailModifier.background(Color.LightGray)
                        }
                        GradingThumbnail(
                            modifier = thumbnailModifier,
                            grading = gradings[it],
                            onClicked = { clickedGrading ->
                                expanded = false
                                onGradingSelected(clickedGrading)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradingThumbnail(
    modifier: Modifier = Modifier,
    grading: Grading,
    onClicked: (Grading) -> Unit = {},
) {
    var columnSize by remember { mutableStateOf(IntSize.Zero) }

    Column(
        modifier = modifier
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .clickable(
                enabled = true,
                onClickLabel = "Select" + " " + stringResource(grading.name),
            ) {
                onClicked(grading)
            }
            .onGloballyPositioned { coordinates ->
                columnSize = coordinates.size
            },
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceAround,
    ) {

        // Do we really need to provide the grading's title here ?
//        Text(
//            text = stringResource(grading.name),
//        )

        grading.grades.reversed().forEach { grade ->

            var boxSize by remember { mutableStateOf(IntSize.Zero) }


            Canvas(
                modifier = Modifier,
            ) {
                val verticalPad = 1f
                drawRoundRect(
                    color = grade.color,
                    topLeft = Offset(x = 0f, y = 0f + verticalPad),
                    size = Size(
                        width = columnSize.width.toFloat(),
                        height = boxSize.height.toFloat() - verticalPad * 2f,
                    ),
                    cornerRadius = CornerRadius(
                        32f * 0.618f, 32f,
                    ),
                )
            }

            Row(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .onGloballyPositioned { coordinates ->
                        boxSize = coordinates.size
                    },

                ) {

                Text(
                    modifier = Modifier
                        .padding(
                            top = 0.dp, bottom = 0.dp,
                            start = 6.dp, end = 6.dp,
                        ),
                    text = stringResource(grade.name),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = grade.textColor,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    // Hmm ; nope, but why ?
    //uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGradingSelectionRow() {
    var grading by remember { mutableStateOf<Grading>(Grading.Quality7Grading) }
    JmTheme {
        GradingSelectionRow(
            grading = grading,
            onGradingSelected = { selectedGrading ->
                grading = selectedGrading
            },
        )
    }
}

@Preview(
    showBackground = true,
)
@Preview(
    showBackground = true,
    fontScale = 2.0f,
)
@Composable
private fun PreviewGradingThumbnail() {
    JmTheme {
        GradingThumbnail(
            grading = Grading.Quality7Grading,
        )
    }
}
