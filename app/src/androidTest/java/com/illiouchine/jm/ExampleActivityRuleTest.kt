package com.illiouchine.jm

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * This show how to use createAndroidComposeRule<MyActivity>() to test an Activity.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleActivityRuleTest: BaseInstrumentedTest() {

    @get:Rule
    val activityTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchTheApp() {

        activityTestRule.waitForIdle()
        activityTestRule.onNodeWithTag("screen_onboarding").assertExists()
        activityTestRule.waitForIdle()

        activityTestRule.onNodeWithText(getString(R.string.button_next)).assertExists().performClick()
        activityTestRule.waitForIdle()

        activityTestRule.onNodeWithText(getString(R.string.button_next)).assertExists().performClick()
        activityTestRule.waitForIdle()

        activityTestRule.onNodeWithText(getString(R.string.button_next)).assertExists().performClick()
        activityTestRule.waitForIdle()

        activityTestRule.onNodeWithText(getString(R.string.button_finish)).assertExists().performClick()
        activityTestRule.waitForIdle()

        activityTestRule.onNodeWithTag("screen_home").assertExists()

//        activityTestRule.onNodeWithTag("home_fab").assertExists()
//        activityTestRule.onNodeWithTag("home_fab").performClick()
//        activityTestRule.waitForIdle()
//        activityTestRule.onNodeWithTag("screen_setup").assertExists()

    }
}