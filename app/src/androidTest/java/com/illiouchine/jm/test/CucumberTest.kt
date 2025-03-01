package com.illiouchine.jm.test

import android.os.Bundle
import android.util.Log
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.MonitoringInstrumentation
import com.illiouchine.jm.ActivityScenarioHolder
import com.illiouchine.jm.MainActivity
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.junit.CucumberOptions
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton


//@WithJunitRule("not @CustomComposable")
//@WithJunitRule()
//@Singleton
//class ComposeRuleHolder @Inject constructor() {
//    @get:Rule(order = 1)
//    val rule = createAndroidComposeRule<MainActivity>()
//}

@WithJunitRule()
class CucumberSteps(
    val scenarioHolder: ActivityScenarioHolder,
) : SemanticsNodeInteractionsProvider {

//class CucumberTest : MonitoringInstrumentation() {
//    private val instrumentationCore: CucumberInstrumentationCore = CucumberInstrumentationCore(this)
//    override fun onCreate(arguments: Bundle) {
//        super.onCreate(arguments)
//
//        instrumentationCore.create(arguments)
//        start()
//    }
//    override fun onStart() {
//        super.onStart()
//        waitForIdleSync()
//        instrumentationCore.start()
//    }

    @get:Rule(order = 1)
    val rule = createAndroidComposeRule<MainActivity>()

//    @Inject
//    lateinit var compose: ComposeRuleHolder

    @Given("^I launch the app$")
    fun initializeApp(){

//        Log.w("tests", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") // nope, does nothing

        // This is actually required for the rule to work, and I don't know how it's injected.
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        scenarioHolder.launch(MainActivity.create(instrumentation.targetContext))
    }

    @Then("^I should see the node tagged \"([^\"]+)\"$")
    fun thenActorSeeNodeByTag(tag: String){
        rule.waitForIdle()
        rule.onNodeWithTag(tag).assertExists()
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