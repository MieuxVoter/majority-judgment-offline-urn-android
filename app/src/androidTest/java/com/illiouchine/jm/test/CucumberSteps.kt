package com.illiouchine.jm.test

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.platform.app.InstrumentationRegistry
import com.illiouchine.jm.MainActivity
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.WithJunitRule


@WithJunitRule
class CucumberSteps(
    private val scenarioHolder: ActivityScenarioHolder,
    private val ruleHolder: ComposeRuleHolder,
) : SemanticsNodeInteractionsProvider {

    private val rule
        get() = ruleHolder.rule

    @Given("^I launch the app$")
    fun initializeApp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // This is actually required for the rule to work, and I don't know how it's injected.
        scenarioHolder.launch(MainActivity.create(instrumentation.targetContext))
    }

    @When("^I wait for idle$")
    fun waitForIdle() {
        rule.waitForIdle()
    }

    @When("^I wait for (?<seconds>[0-9]+[.]?[0-9]*|[0-9]*[.]?[0-9]+) ?s$")
    fun waitForSeconds(seconds: Float) {
        val duration = (seconds * 1000f).toLong()
        rule.waitUntil(duration * 2) {
            Thread.sleep(duration)
            true
        }
    }

    @Then("^I should(?: still)?(?<negation> not|) see the node tagged \"(?<tag>[^\"]+)\"$")
    fun thenActorShouldSeeNodeByTag(negation: String, tag: String) {
        if (negation != "") {
            rule.onNodeWithTag(tag).assertDoesNotExist()
        } else {
            rule.onNodeWithTag(tag).assertExists()
        }
    }

    @Then("^I should(?: still| now)?(?<negation> not|) see the (?<name>.+?) screen(?: anymore)?$")
    fun thenActorShouldSeeScreen(negation: String, name: String) {
        val tag = "${name.replace(Regex("\\s+"), "_")}_screen"
        if (negation != "") {
            rule.onNodeWithTag(tag).assertDoesNotExist()
        } else {
            rule.onNodeWithTag(tag).assertExists()
        }
    }

    // This is not useful, as it is dependant on the language of the emulator.
//    @Then("^I should(?<negation> not|) see the node with text \"(?<text>.+)\"$")
//    fun thenActorShouldSeeNodeByText(negation: String, text: String) {
//        if (negation != "") {
//            rule.onNodeWithText(text).assertDoesNotExist()
//        } else {
//            rule.onNodeWithText(text).assertExists()
//        }
//    }

    @When("^I click on the node tagged \"([^\"]+)\"$")
    fun whenActorClicksNodeByTag(tag: String) {
        rule.onNodeWithTag(tag).assertExists().performClick()
    }

    @When("^I scroll to the node tagged \"([^\"]+)\"\$")
    fun whenActorScrolls(tag: String) {
        var current = rule.onNodeWithTag(tag)
        var scrolled = false
        var bestEffort = 64 // max depth, "just in case"©

        while (0 < bestEffort--) {
            try {
                current.performScrollToNode(
                    matcher = hasTestTag(tag),
                )
                scrolled = true
            } catch (_: AssertionError) {
                // EAFP pattern 'cause simpler here ; fix at will
            }

            if (scrolled or current.fetchSemanticsNode().isRoot) {
                break
            }

            current = current.onParent()
        }

        assert(scrolled) { "Did not perform any scroll." }
    }

    override fun onAllNodes(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean,
    ): SemanticsNodeInteractionCollection {
        return rule.onAllNodes(
            matcher = matcher,
            useUnmergedTree = useUnmergedTree,
        )
    }

    override fun onNode(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean,
    ): SemanticsNodeInteraction {
        return rule.onNode(
            matcher = matcher,
            useUnmergedTree = useUnmergedTree,
        )
    }
}
