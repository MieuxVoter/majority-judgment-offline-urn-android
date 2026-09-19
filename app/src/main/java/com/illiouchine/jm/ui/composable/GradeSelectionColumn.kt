package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.extensions.reversedIf
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun GradeSelectionColumn(
    modifier: Modifier = Modifier,
    gradeModifier: Modifier = Modifier,
    grading: Grading,
    gradeSemanticsLabels: List<String>? = null,
    gradeTestTagPrefix: String = "grade_selection_button_",
    highestGradeUpTop: Boolean = false,
    onGradeSelected: (gradeIndex: Int) -> Unit = {},
) {
    if (gradeSemanticsLabels != null) {
        require(gradeSemanticsLabels.size == grading.grades.size)
    }

    Column(
        modifier = modifier,
    ) {

        for (gradeIndex in (0..<grading.grades.size).reversedIf(highestGradeUpTop)) {

            val gradeName = stringResource(id = grading.getGradeName(gradeIndex))

            GradeSelectionButton(
                modifier = gradeModifier
                    .testTag("${gradeTestTagPrefix}${gradeIndex}")
                    .fillMaxWidth()
                    .semantics {
                        onClick(
                            label = if (gradeSemanticsLabels != null) {
                                gradeSemanticsLabels[gradeIndex]
                            } else {
                                gradeName
                            },
                            action = null,
                        )
                    },
                text = gradeName.uppercase(),
                bgColor = grading.getGradeColor(gradeIndex),
                fgColor = grading.getGradeTextColor(gradeIndex),
                onClick = {
                    onGradeSelected(gradeIndex)
                },
            )
        }
    }
}

@Preview()
@Composable
private fun PreviewGradeSelectionColumn7() {
    JmTheme {
        GradeSelectionColumn(
            grading = Grading.Quality5Grading,
            onGradeSelected = {},
        )
    }
}
