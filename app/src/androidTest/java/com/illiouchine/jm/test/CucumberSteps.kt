package com.illiouchine.jm.test

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.illiouchine.jm.MainActivity
import com.illiouchine.jm.data.room.PollDataBase
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.WithJunitRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals


@WithJunitRule
class CucumberSteps(
    private val scenarioHolder: ActivityScenarioHolder,
    private val ruleHolder: ComposeRuleHolder,
    // Sadly, nope, we get:
    // org.picocontainer.injectors.AbstractInjector$UnsatisfiableDependenciesException:
    // com.illiouchine.jm.test.CucumberSteps has unsatisfied dependency
    // 'interface com.illiouchine.jm.data.room.PollDao'
    //private val pollDao: PollDao,

) : SemanticsNodeInteractionsProvider {

    // We need a rule holder for scoping and bypassing the one-@Rule-per-class limitation.
    private val rule
        get() = ruleHolder.rule

    private lateinit var pollDb: PollDataBase

    private fun getAppContext(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun before() {
        // 1. Get the dep from Koin
        // … how ?

        // 2. Build it ourselves  (urgh)
        pollDb = Room.databaseBuilder(
            context = getAppContext(),
            klass = PollDataBase::class.java,
            name = "PollDataBase",
        ).build()
    }

    @Given("^I launch the app$")
    fun initializeApp() {
        // This is required for the rule to work.
        scenarioHolder.launch(MainActivity.create(getAppContext()))
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
    // But if we can manage to fetch a R.string.<something> from "something" as String… Might work.
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
        var current = rule.onNodeWithTag(tag).assertExists()
        var scrolled = false
        var bestEffort = 64 // max depth, "just in case"©

        // We're going to try every parent 'til one can scroll.
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

    @Then("^there should be (?<amount>[0-9]+?) polls? in the database$")
    fun thenThereShouldBePollsInDb(amount: Int) {
        runTest {
            val actual = pollDb.pollDao().loadPolls().size
            assertEquals(amount, actual)
        }
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
