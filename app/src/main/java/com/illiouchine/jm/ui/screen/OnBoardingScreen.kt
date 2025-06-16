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
import androidx.compose.runtime.LaunchedEffect
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
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


private fun PagerState.isScrollOverLast(): Boolean =
    (this.currentPage == this.pageCount -1 && this.currentPageOffsetFraction > 0)

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

        LaunchedEffect(
            pagerState.isScrollInProgress,
        ) {
            if (pagerState.isScrollOverLast()) {
                onFinish()
            }
        }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(Theme.spacing.medium),
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
                with(onBoardingPages[page]) {
                    OnBoardingPage(
                        modifier = Modifier,
                        pagerState = pagerState,
                        pageIndex = page,
                        onBoardingPage = this,
                    )
                }
            }
            OnBoardingBottomRow(
                modifier = Modifier.fillMaxWidth(),
                pagerState = pagerState,
                onFinish = { onFinish() },
            )
        }
    }
}

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
fun OnBoardingPage(
    modifier: Modifier,
    pagerState: PagerState,
    pageIndex: Int,
    onBoardingPage: OnBoardingPage,
) {
    Box(
        modifier = modifier.graphicsLayer {
            // Calculate the absolute offset for the current page from the
            // scroll position. We use the absolute value which allows us to mirror
            // any effects for both directions
            val pageOffset = pagerState.calculateCurrentOffsetForPage(pageIndex).absoluteValue
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
            modifier = modifier
                .align(Alignment.Center)
                .padding(horizontal = Theme.spacing.medium + Theme.spacing.small),
        ) {
            Image(
                painter = painterResource(onBoardingPage.image),
                contentDescription = null, // anything but silence would be noise
            )
            Spacer(Modifier.padding(Theme.spacing.medium))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(onBoardingPage.text),
            )
        }
    }
}

// extension method for current page offset
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
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
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (pagerState.currentPage == onBoardingPages.lastIndex) {
            Spacer(modifier = Modifier.weight(0.8f))
        } else {
            TextButton(
                modifier = Modifier
                    .weight(0.8f)
                    .testTag("onboarding_screen_skip"),
                onClick = { onFinish() },
            ) { Text(stringResource(R.string.button_skip)) }
        }

        ViewPager(
            modifier = Modifier
                .weight(0.9f)
                .wrapContentSize(),
            pageSize = onBoardingPages.size,
            currentPage = pagerState.currentPage,
        )

        if (pagerState.currentPage == onBoardingPages.lastIndex) {
            TextButton(
                modifier = Modifier
                    .weight(0.8f)
                    .wrapContentSize()
                    .testTag("onboarding_screen_finish"),
                onClick = { onFinish() },
            ) { Text(stringResource(R.string.button_finish)) }
        } else {
            TextButton(
                modifier = Modifier
                    .weight(0.8f)
                    .wrapContentSize()
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
    JmTheme {
        OnBoardingScreen(
            modifier = modifier,
        )
    }
}

@Preview(
    showSystemUi = true,
    device = "spec:width=330dp,height=691dp",
//    widthDp = 330,
    locale = "fr",
)
@Composable
fun PreviewOnBoardingLastPageFr(modifier: Modifier = Modifier) {
    JmTheme {
        OnBoardingScreen(
            modifier = modifier,
        )
    }
}
