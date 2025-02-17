package com.illiouchine.jm.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.JmTheme

data class OnBoardingPage(
    val image: Int, // Todo use Resource
    val text : String,
)

val onBoardingPages = listOf(
    OnBoardingPage(0, "Le Jugement Majoritaire vous souhaite la bienvenue."),
    OnBoardingPage(1, "Ici votre vie sera jugée par un tribunal populaire."),
    OnBoardingPage(2, "Prêt⋅e ?"),
)

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onFinish:() -> Unit= {}
) {

    var currentOnBoardingIndex by remember { mutableIntStateOf(0) }

    Box(modifier = modifier
        .fillMaxSize()
//        .background(Color.White)
        .padding(36.dp)
    ){
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = "Majority Judgment"
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = onBoardingPages[currentOnBoardingIndex].text
        )

        ViewPager(
            modifier = Modifier.align(Alignment.BottomCenter),
            pageSize = onBoardingPages.size,
            currentPage = currentOnBoardingIndex
        )

        if (currentOnBoardingIndex == onBoardingPages.size -1){
            Button(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {onFinish()}
            ) { Text("finish") }
        } else {
            Button(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = { currentOnBoardingIndex++ }
            ) { Text("next") }
        }
    }
}

@Preview
@Composable
fun ViewPager(
    modifier: Modifier = Modifier,
    pageSize: Int = 3,
    currentPage: Int = 0,
) {
    Row(modifier = modifier
//        .background(Color.White)
        .padding(16.dp)
    ){
        for (i in 0 until pageSize) {
            val color = if (i == currentPage) { Color.Gray } else { Color.LightGray }
            Spacer(modifier = Modifier.size(4.dp))
            Box(modifier = Modifier
                .size(16.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color)
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

@Preview
@Composable
fun PreviewOnBoarding(modifier: Modifier = Modifier) {
    JmTheme {
        OnBoardingScreen()
    }
}