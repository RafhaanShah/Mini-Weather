package com.miniweather.testutil

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.miniweather.activity.TestActivity
import org.junit.Rule

class BaseFlowTest : BaseAndroidTest() {

    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<TestActivity> =
        ActivityScenarioRule(TestActivity::class.java)

    protected fun launchFragment(@IdRes startDestId: Int, fragmentArgs: Bundle? = null) {
        activityScenarioRule.scenario.onActivity {
            it.navigateToFragment(startDestId, fragmentArgs)
        }
    }
}
