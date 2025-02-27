package com.illiouchine.jm

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val activityTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.illiouchine.jm", context.packageName)
    }

    @Test
    fun makeNewPollUsingFab() {

//        activityTestRule.setContent {
//            JmTheme {
//                HomeScreen(
//                    onSetupBlankPoll = {
//                    },
//                )
//            }
//        }

        // FIXME:

//        activityTestRule.waitForIdle()
//        activityTestRule.onNodeWithTag("screen_home").assertExists()
//        activityTestRule.onNodeWithTag("home_fab").assertExists()
//        activityTestRule.onNodeWithTag("home_fab").performClick()
//        activityTestRule.waitForIdle()
//        activityTestRule.onNodeWithTag("screen_setup").assertExists()

    }

    @Test
    fun testHomeScreen(
//        activityLauncher=
    ) {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
        composeTestRule.onNodeWithTag("home_fab").assertExists()
        assertEquals(0, onSetupBlankPollTriggers)
        composeTestRule.onNodeWithTag("home_fab").performClick()
        assertEquals(1, onSetupBlankPollTriggers)

        // We can also fetch nodes by text, but we need to use translated strings.
        composeTestRule.onNodeWithText(
            context.getString(R.string.button_new_poll),
            useUnmergedTree = true,
        ).assertExists()
    }
}