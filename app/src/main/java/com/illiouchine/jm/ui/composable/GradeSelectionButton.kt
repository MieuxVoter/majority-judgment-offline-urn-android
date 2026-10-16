package com.illiouchine.jm.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.ui.composable.spacer.SmallVerticalSpacer
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun GradeSelectionButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "",
    bgColor: Color = Color.Red,
    fgColor: Color = Color.White,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        colors = ButtonColors(
            containerColor = bgColor,
            contentColor = fgColor,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White,
        ),
    ) {
        Text(
            text = text,
//            fontSize = 24.sp, // experimenting with autoSize instead
            autoSize = TextAutoSize.StepBased(
                minFontSize = 8.sp,
                maxFontSize = 80.sp,
            ),
            maxLines = 1,
        )
        // Example with outline
//        Text(
//            text = text,
//            fontSize = 5.em,
//            style = TextStyle.Default.copy(
//                fontSize = 64.sp,
//                drawStyle = Stroke(
//                    miter = 10f,
//                    width = 5f,
//                    join = StrokeJoin.Round,
//                )
//            )
//        )
    }
}

@Preview
@Composable
private fun PreviewGradeSelectionButton() {
    JmTheme {
        GradeSelectionButton(
            text = "Excellent",
            bgColor = Color.Red,
            fgColor = Color.White,
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 2.0f,
)
@Composable
private fun PreviewGradeSelectionButtonBigFont() {
    JmTheme {
        Column(
            Modifier.padding(32.dp)
        ) {
            GradeSelectionButton(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                ,
                text = "SOMEWHAT GOOD",
            )
            SmallVerticalSpacer()
            GradeSelectionButton(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                ,
                text = "SOMEWHAT EXTRA LONG",
                bgColor = Color.Red,
                fgColor = Color.White,
            )
            SmallVerticalSpacer()
            GradeSelectionButton(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                ,
                text = "SOMEWHAT EXTRA ABSURDLY LONG OMG THIS IS HIDDEN — HELLO WORLD :)",
                bgColor = Color.Red,
                fgColor = Color.White,
            )
            SmallVerticalSpacer()
            GradeSelectionButton(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                ,
                text = "GOOD",
                bgColor = Color.DarkGray,
                fgColor = Color.White,
            )
            SmallVerticalSpacer()
            GradeSelectionButton(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                ,
                text = "VERY GOOD",
                bgColor = Color.DarkGray,
                fgColor = Color.White,
            )
        }
    }
}
