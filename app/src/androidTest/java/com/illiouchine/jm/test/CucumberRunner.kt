package com.illiouchine.jm.test

import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.CucumberOptions

/**
 * The CucumberOptions annotation is mandatory for exactly one of the classes in the test project.
 * Only the first annotated class that is found will be used, others are ignored. If no class is
 * annotated, an exception is thrown. This annotation does not have to placed in runner class
 */
@CucumberOptions(
    features = ["features"],
    glue = ["com.illiouchine.jm.test"],
)
class CucumberRunner : CucumberAndroidJUnitRunner() {

    // nothing is cool

}
