package com.illiouchine.jm.unused

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.illiouchine.jm.MainActivity
import com.illiouchine.jm.R
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
    val rule = createAndroidComposeRule<MainActivity>()

    // Note: the order of the tests matter, since they share the TestComposeRule.
    // skimOnboarding should always be first, so onboarding is flagged as done for later tests.

    // I'm not sure about all this ; feel free to experiment.

    @Test
    fun aaaSkimOnboarding() {
        // When we launch the application for the first time

        // Then we should be on the onboarding screen
        rule.onNodeWithTag("onboarding_screen").assertExists()
        rule.waitForIdle()

        rule.onNodeWithText(getString(R.string.button_next)).assertExists().performClick()
        rule.waitForIdle()

        rule.onNodeWithText(getString(R.string.button_next)).assertExists().performClick()
        rule.waitForIdle()

        rule.onNodeWithText(getString(R.string.button_next)).assertExists().performClick()
        rule.waitForIdle()

        // When we click on the node with text button_finish
        // When we click on button_finish
        rule.onNodeWithText(getString(R.string.button_finish)).assertExists().performClick()
        rule.waitForIdle()

        // Then we should be on the home screen
        rule.onNodeWithTag("home_screen").assertExists()
    }

    @Test
    fun whooshSomePoll() {
        rule.onNodeWithTag("home_fab").assertExists().performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("setup_screen").assertExists()

        // … more tests
    }
}