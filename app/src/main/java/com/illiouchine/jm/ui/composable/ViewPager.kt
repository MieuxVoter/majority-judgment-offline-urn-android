package com.illiouchine.jm.ui.composable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing


@Composable
fun ViewPager(
    modifier: Modifier = Modifier,
    pageSize: Int = 3,
    currentPage: Int = 0,
) {

    Row(
        modifier = modifier
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pageSize){ iteration ->
            val animatedSize by animateDpAsState(
                targetValue = if (currentPage == iteration) {
                    24.dp
                } else {
                    16.dp
                }
            )
            val color = if (iteration == currentPage) {
                Color.Gray
            } else {
                Color.LightGray
            }
            Spacer(modifier = Modifier.size(Theme.spacing.extraSmall))
            Box(
                modifier = Modifier
                    .size(animatedSize)
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.size(Theme.spacing.extraSmall))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewViewPager() {
    JmTheme { ViewPager(pageSize = 4, currentPage = 2) }
}
