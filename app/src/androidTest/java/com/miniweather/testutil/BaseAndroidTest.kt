package com.miniweather.testutil

import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.miniweather.R
import com.miniweather.activity.TestActivity
import org.junit.Before
import org.junit.Rule
import org.junit.rules.Timeout
import org.junit.runner.RunWith

abstract class BaseAndroidTest {

    private val instrumentationContext: Context = getInstrumentation().context
    protected val appContext: Context = getApplicationContext()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(30)

    @get:Rule
    val storagePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule
    val testFailureScreenshotRule = TestFailureScreenshotRule(appContext.contentResolver)

    @get:Rule
    val webServerRule = MockWebServerRule()

    protected val mocksHandler = TestMocksHandler(appContext, instrumentationContext, webServerRule)

    @CallSuper
    @Before
    open fun setup() {
        mocksHandler.initialise()
    }
}

@RunWith(AndroidJUnit4::class)
abstract class BaseIntegrationTest<T : Fragment>(private val clazz: Class<T>) : BaseAndroidTest() {

    protected val navController = TestNavHostController(appContext)

    fun launchFragment(
        fragmentArgs: Bundle? = null,
        @NavigationRes navGraph: Int = R.navigation.nav_graph_main
    ) {
        FragmentLauncher.launchFragment(
            clazz,
            clazz.newInstance(),
            fragmentArgs,
            navController,
            navGraph
        )
    }
}

class BaseFlowTest : BaseAndroidTest() {

    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<TestActivity> =
        ActivityScenarioRule(TestActivity::class.java)

    protected fun launchFragment(
        @NavigationRes navGraphId: Int = R.navigation.nav_graph_main,
        @IdRes startDestId: Int,
        fragmentArgs: Bundle? = null
    ) {
        activityScenarioRule.scenario.onActivity {
            it.navigateToFragment(navGraphId, startDestId, fragmentArgs)
        }
    }
}
