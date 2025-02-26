package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.composable.ViewPager
import com.illiouchine.jm.ui.theme.JmTheme

data class OnBoardingPage(
    val image: Int, // Todo use Resource
    val text: String,
)


@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
) {

    val onBoardingPages = listOf(
        OnBoardingPage(0, stringResource(R.string.onboarding_welcome_to_your_offline_poll_app)),
        OnBoardingPage(1, stringResource(R.string.onboarding_setup_a_poll_and_share_the_phone)),
        OnBoardingPage(2, stringResource(R.string.onboarding_this_is_free_software)),
        OnBoardingPage(3, stringResource(R.string.onboarding_ready)),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        var currentPageIndex by remember { mutableIntStateOf(0) }
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(36.dp),
        ) {
            Text(
                modifier = Modifier.align(Alignment.TopStart),
                text = stringResource(R.string.majority_judgment),
            )

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = onBoardingPages[currentPageIndex].text,
            )

            ViewPager(
                modifier = Modifier.align(Alignment.BottomCenter),
                pageSize = onBoardingPages.size,
                currentPage = currentPageIndex,
            )

            if (currentPageIndex == onBoardingPages.size - 1) {
                Button(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = { onFinish() },
                ) { Text(stringResource(R.string.button_finish)) }
            } else {
                Button(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = { currentPageIndex++ },
                ) { Text(stringResource(R.string.button_next)) }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewOnBoarding(modifier: Modifier = Modifier) {
    JmTheme { OnBoardingScreen() }
}