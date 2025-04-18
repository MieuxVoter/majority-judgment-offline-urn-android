package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun GradeSelectionButton(
    modifier: Modifier = Modifier,
    height: Dp = 64.dp,
    enabled: Boolean = true,
    text: String = "",
    bgColor: Color = Color.Red,
    fgColor: Color = Color.White,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(top = 12.dp),
        enabled = enabled,
        onClick = { onClick() },
        colors = ButtonColors(
            containerColor = bgColor,
            contentColor = fgColor,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White,
        ),
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
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
            text = "Candidat A",
            bgColor = Color.Red,
            fgColor = Color.White,
        )
    }
}
