package com.illiouchine.jm.unused

import org.junit.Assert
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleInstrumentedTest: BaseInstrumentedTest() {
    @Test
    fun useAppContext() {
        Assert.assertEquals("com.illiouchine.jm", getContext().packageName)
    }
}