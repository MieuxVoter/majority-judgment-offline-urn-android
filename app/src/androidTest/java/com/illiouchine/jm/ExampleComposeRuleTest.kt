package com.illiouchine.jm

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * This example test shows how to use createComposeRule() to test Composables in isolation.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleComposeRuleTest : BaseInstrumentedTest() {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHomeScreen() {

        var onSetupBlankPollTriggers = 0
        composeTestRule.setContent {
            JmTheme {
                HomeScreen(
                    onSetupBlankPoll = {
                        onSetupBlankPollTriggers++
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag("screen_home").assertExists()
        assertEquals(0, onSetupBlankPollTriggers)
        composeTestRule.onNodeWithTag("home_fab").assertExists().performClick()
        assertEquals(1, onSetupBlankPollTriggers)

        // We can also fetch nodes by text, but we need to use translated strings.
        composeTestRule.onNodeWithText(
            getString(R.string.button_new_poll),
            useUnmergedTree = true,
        ).assertExists().performClick()
        assertEquals(2, onSetupBlankPollTriggers)
    }
}