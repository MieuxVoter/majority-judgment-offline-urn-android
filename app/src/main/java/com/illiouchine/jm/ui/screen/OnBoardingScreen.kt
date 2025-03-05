package com.illiouchine.jm.ui.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.composable.ViewPager
import com.illiouchine.jm.ui.theme.JmTheme

data class OnBoardingPage(
    @DrawableRes val image: Int,
    @StringRes val text: Int,
)

val onBoardingPages = listOf(
    OnBoardingPage(R.drawable.onboarding_0, R.string.onboarding_welcome_to_your_offline_poll_app),
    OnBoardingPage(R.drawable.onboarding_1, R.string.onboarding_setup_a_poll_and_share_the_phone),
    OnBoardingPage(R.drawable.onboarding_2, R.string.onboarding_this_is_free_software),
    OnBoardingPage(R.drawable.onboarding_3, R.string.onboarding_ready),
)

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_onboarding"),
    ) { innerPadding ->

        var currentPageIndex by remember { mutableIntStateOf(0) }
        var dragValue by remember { mutableFloatStateOf(0f) }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                text = stringResource(R.string.majority_judgment),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset { IntOffset(x = (-(dragValue * 0.62)).toInt(), y = 0) }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                when {
                                    dragValue > 0 && currentPageIndex >= (onBoardingPages.size - 1) -> onFinish()
                                    dragValue > 0 && currentPageIndex < (onBoardingPages.size - 1) -> currentPageIndex++
                                    dragValue < 0 && currentPageIndex > 0 -> currentPageIndex--
                                }
                                dragValue = 0f
                            },
                            onDrag = { _, dragAmount ->
                                dragValue += -dragAmount.x
                            }
                        )
                    },
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                ) {
                    Image(
                        painter = painterResource(onBoardingPages[currentPageIndex].image),
                        contentDescription = "", // anything but silence would be noise
                    )
                    Spacer(Modifier.padding(16.dp))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(onBoardingPages[currentPageIndex].text),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Spacer(Modifier.weight(0.7f))
                ViewPager(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentSize(),
                    pageSize = onBoardingPages.size,
                    currentPage = currentPageIndex,
                )

                if (currentPageIndex == onBoardingPages.size - 1) {
                    TextButton(
                        modifier = Modifier.weight(0.7f).testTag("screen_onboarding_finish"),
                        onClick = { onFinish() },
                    ) { Text(stringResource(R.string.button_finish)) }
                } else {
                    TextButton(
                        modifier = Modifier.weight(0.7f).testTag("screen_onboarding_next"),
                        onClick = { currentPageIndex++ },
                    ) { Text(stringResource(R.string.button_next)) }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewOnBoarding(modifier: Modifier = Modifier) {
    JmTheme { OnBoardingScreen() }
}