package com.illiouchine.jm.ui.composable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun GradeSelectionList(
    pollConfig: PollConfig,
    forProposalIndex: Int,
    onGradeSelected: (Int) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.all_things_considered_i_think),
            fontStyle = FontStyle.Italic,
        )
    }
    // Using Row here instead won't center the text on the phone, even though it does in the preview
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = pollConfig.proposals[forProposalIndex],
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            lineHeight = 24.sp,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.verb_is),
            fontStyle = FontStyle.Italic,
        )
    }

    val context = LocalContext.current
    var selectedGradeIndex: Int? by remember { mutableStateOf(null) }

    for (gradeIndex in 0..<pollConfig.grading.grades.size) {
        val interactionSource = remember { MutableInteractionSource() }
        val interactionSourceIsPressed by interactionSource.collectIsFocusedAsState()
        val coroutine = rememberCoroutineScope()

        val animatedHeight by animateDpAsState(
            targetValue = if (interactionSourceIsPressed) 80.dp else 64.dp
        )
        GradeSelectionButton(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("grade_selection_$gradeIndex"),
            height = animatedHeight,
            enabled =  ((selectedGradeIndex == null) || (selectedGradeIndex == gradeIndex)),
            text = context.getString(pollConfig.grading.grades[gradeIndex].name).uppercase(),
            bgColor = pollConfig.grading.getGradeColor(gradeIndex),
            fgColor = pollConfig.grading.getGradeTextColor(gradeIndex)
        ) {
            if (selectedGradeIndex == null) {
                selectedGradeIndex = gradeIndex
                coroutine.launch {
                    delay(150)
                    onGradeSelected(gradeIndex)
                    selectedGradeIndex = null
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview7GradeList() {
    JmTheme {
        Column {
            GradeSelectionList(
                pollConfig = PollConfig(
                    subject = "toto ?",
                    proposals = listOf("A", "B", "C"),
                    grading = DEFAULT_GRADING_QUALITY_VALUE,
                ),
                forProposalIndex = 2,
                onGradeSelected = {}
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview5GradeList() {
    JmTheme {
        Column {
            GradeSelectionList(
                pollConfig = PollConfig(
                    subject = "toto ?",
                    proposals = listOf("A", "B", "C"),
                    grading = DEFAULT_GRADING_QUALITY_VALUE,
                ),
                forProposalIndex = 2,
                onGradeSelected = {}
            )
        }
    }
}