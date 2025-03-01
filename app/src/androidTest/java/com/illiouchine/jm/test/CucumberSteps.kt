package com.illiouchine.jm.test

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.illiouchine.jm.MainActivity
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.WithJunitRule
import org.junit.Rule


// We're probably going to need a rule holder if we want multiple step files.
//@WithJunitRule()
//@Singleton
//class ComposeRuleHolder @Inject constructor() {
//    @get:Rule(order = 1)
//    val rule = createAndroidComposeRule<MainActivity>()
//}

@WithJunitRule
class CucumberSteps(
    val scenarioHolder: ActivityScenarioHolder,
) : SemanticsNodeInteractionsProvider {

    @get:Rule(order = 1)
    val rule = createAndroidComposeRule<MainActivity>()

//    @Inject
//    lateinit var compose: ComposeRuleHolder

    @Given("^I do nothing$")
    fun doNothing() {}

    @Given("^I wait for idle$")
    fun waitForIdle() {
        rule.waitForIdle()
    }

    @Given("I launch the app")
    fun initializeApp() {

//        Log.w("tests", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") // nope, does nothing

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // This is actually required for the rule to work, and I don't know how it's injected.
        scenarioHolder.launch(MainActivity.create(instrumentation.targetContext))
    }

    @Then("^I should see the node tagged \"(?<tag>[^\"]+)\"$")
    fun thenActorShouldSeeNodeByTag(tag: String) {
        rule.onNodeWithTag(tag).assertExists()
    }

    @Then("^I should not see the node tagged \"(?<tag>[^\"]+)\"$")
    fun thenActorShouldNotSeeNodeByTag(tag: String) {
        rule.onNodeWithTag(tag).assertDoesNotExist()
    }

    @When("^I click on the node tagged \"([^\"]+)\"$")
    fun whenActorClicksNodeByTag(tag: String) {
        rule.onNodeWithTag(tag).assertExists().performClick()
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