package com.illiouchine.jm.ui.composable

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun EditableJudgmentSummary(
    modifier: Modifier = Modifier,
    proposalName: String,
    gradeIndex: Int,
    grading: Grading,
    showGrades: Boolean = false,
    onGradeChange: (newGradeIndex: Int) -> Unit = {},
) {
    // Keep an internal state of the visibility of the grades, yet allow the caller to change it.
    var gradesAreVisible by remember(showGrades) { mutableStateOf(showGrades) }

    Column {
        JudgmentSummary(
            modifier = modifier
                .clickable(
                    enabled = true,
                    onClickLabel = "change the grade",
                    role = Role.DropdownList,
                    onClick = {
                        gradesAreVisible = !gradesAreVisible
                    },
                ),
            proposalName = proposalName,
            gradeString = stringResource(grading.getGradeName(gradeIndex)),
            color = grading.getGradeColor(gradeIndex),
        )

        AnimatedVisibility(
            visible = gradesAreVisible,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.medium),
            ) {
                GradeSelectionColumn(
                    grading = grading,
                    gradeModifier = Modifier
                        .padding(vertical = Theme.spacing.extraSmall + Theme.spacing.tiny)
                        .height(height = Theme.spacing.extraLarge),
                    onGradeSelected = { gradeIndex ->
                        gradesAreVisible = false
                        onGradeChange(gradeIndex)
                    },
                )
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewJudgmentSummaryLongName() {
    JmTheme {
        Column {
            var gradeIndexA by remember { mutableIntStateOf(3) }
            EditableJudgmentSummary(
                proposalName = "That candidate with a long name, so long it eats the end of the sentence",
                grading = Grading.Quality7Grading,
                gradeIndex = gradeIndexA,
                onGradeChange = {
                    gradeIndexA = it
                },
            )
            var gradeIndexB by remember { mutableIntStateOf(6) }
            EditableJudgmentSummary(
                proposalName = "Théo Sabattie",
                grading = Grading.Quality7Grading,
                gradeIndex = gradeIndexB,
                onGradeChange = {
                    gradeIndexB = it
                },
            )
        }
    }
}
