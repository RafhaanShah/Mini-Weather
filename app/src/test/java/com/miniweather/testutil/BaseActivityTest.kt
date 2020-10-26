package com.miniweather.testutil

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Before
import org.junit.Rule

abstract class BaseActivityTest<T : AppCompatActivity>(clazz: Class<T>) : BaseInstrumentedTest() {

    @get:Rule
    val activityRule = ActivityScenarioRule(clazz)

    protected lateinit var scenario: ActivityScenario<T>

    @CallSuper
    @Before
    open fun setup() {
        scenario = activityRule.scenario
    }

}
