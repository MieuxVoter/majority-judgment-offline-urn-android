package com.illiouchine.jm

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleInstrumentedTest: BaseInstrumentedTest() {
    @Test
    fun useAppContext() {
        assertEquals("com.illiouchine.jm", getContext().packageName)
    }
}