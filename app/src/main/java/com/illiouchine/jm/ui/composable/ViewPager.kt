package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun ViewPager(
    modifier: Modifier = Modifier,
    pageSize: Int = 3,
    currentPage: Int = 0,
) {
    Row(
        modifier = modifier
            .padding(16.dp)
    ) {
        for (i in 0 until pageSize) {
            val color = if (i == currentPage) {
                Color.Gray
            } else {
                Color.LightGray
            }
            Spacer(modifier = Modifier.size(4.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewViewPager() {
    JmTheme { ViewPager(pageSize = 4, currentPage = 2) }
}
