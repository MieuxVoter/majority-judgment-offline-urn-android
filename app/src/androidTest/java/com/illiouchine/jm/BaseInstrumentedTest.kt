package com.illiouchine.jm

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseInstrumentedTest {
    fun getContext(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }
    fun getString(@StringRes resId: Int): String {
        return getContext().getString(resId)
    }
}