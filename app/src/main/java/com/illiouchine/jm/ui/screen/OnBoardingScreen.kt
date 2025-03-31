package com.illiouchine.jm.ui.screen

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.composable.ViewPager
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class OnBoardingPage(
    @DrawableRes val image: Int,
    @StringRes val text: Int,
)

@Stable
val onBoardingPages = listOf(
    OnBoardingPage(R.drawable.onboarding_0, R.string.onboarding_welcome_to_your_offline_poll_app),
    OnBoardingPage(R.drawable.onboarding_1, R.string.onboarding_setup_a_poll_and_share_the_phone),
    OnBoardingPage(R.drawable.onboarding_2, R.string.onboarding_this_is_free_software),
)

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
) {
    BackHandler {
        onFinish()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
    ) { innerPadding ->
        val pagerState = rememberPagerState(pageCount = { onBoardingPages.size })
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            ScreenTitle(text = stringResource(R.string.majority_judgment))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(0.dp),
                pageSpacing = 20.dp,
            ) { page ->
                val onBoardingPage = onBoardingPages[page]
                Box(
                    modifier = modifier.graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                ).absoluteValue
                        alpha = lerp(
                            start = 0.1f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = lerp(
                            start = 0.3f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleY = lerp(
                            start = 0.3f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                    ) {
                        Image(
                            painter = painterResource(onBoardingPage.image),
                            contentDescription = null, // anything but silence would be noise
                        )
                        Spacer(Modifier.padding(16.dp))
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = stringResource(onBoardingPage.text),
                        )
                    }
                }
            }
            OnBoardingBottomRow(
                modifier = Modifier.fillMaxWidth(),
                pagerState = pagerState,
                onFinish = { onFinish() }
            )
        }
    }
}

@Composable
fun OnBoardingBottomRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onFinish: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        TextButton(
            modifier = Modifier
                .weight(0.7f)
                .testTag("onboarding_screen_skip"),
            onClick = { onFinish() },
        ) { Text("Skip") }

        ViewPager(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(),
            pageSize = onBoardingPages.size,
            currentPage = pagerState.currentPage,
        )

        if (pagerState.currentPage == onBoardingPages.size - 1) {
            TextButton(
                modifier = Modifier
                    .weight(0.7f)
                    .testTag("onboarding_screen_finish"),
                onClick = { onFinish() },
            ) { Text(stringResource(R.string.button_finish)) }
        } else {
            TextButton(
                modifier = Modifier
                    .weight(0.7f)
                    .testTag("onboarding_screen_next"),
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
            ) { Text(stringResource(R.string.button_next)) }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewOnBoarding(modifier: Modifier = Modifier) {
    JmTheme { OnBoardingScreen() }
}