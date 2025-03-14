package com.illiouchine.jm.test

import io.cucumber.java.en.Given

class DummySteps {

    @Given("^I do nothing$")
    fun doNothing() {}

    @Given("^I become another participant$")
    fun becomeSomeoneElse() {}

}