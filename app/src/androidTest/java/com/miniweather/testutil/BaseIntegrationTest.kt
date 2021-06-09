package com.miniweather.testutil

import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.miniweather.R
import com.miniweather.app.IntegrationTestApplication
import com.miniweather.database.WeatherDatabase
import com.miniweather.di.TestAppComponent
import com.miniweather.util.Empty
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.Timeout
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseIntegrationTest<T : Fragment>(private val clazz: Class<T>) {

    private val appContext: Context = getApplicationContext()
    private val instrumentationContext: Context = getInstrumentation().context

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(30)

    @get:Rule
    val testFailureScreenshotRule = TestFailureScreenshotRule(appContext.contentResolver)

    @get:Rule
    val storagePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var scenario: FragmentScenario<T>
    protected val mocksHandler = TestMocksHandler()

    @CallSuper
    @Before
    open fun setup() {
        WebServer.start(instrumentationContext.assets)
        mocksHandler.initMocks(
            (appContext as IntegrationTestApplication)
                .getAppComponent() as TestAppComponent
        )
    }

    @CallSuper
    @After
    open fun tearDown() {
        WebServer.shutdown()
        WebServer.verifyRequests()
    }

    protected fun launchFragment(fragmentArgs: Bundle? = null) {
        scenario = FragmentScenario.launchInContainer(
            fragmentClass = clazz,
            fragmentArgs = fragmentArgs,
            themeResId = R.style.AppTheme
        )
    }

    protected fun expectHttpRequest(
        path: String = String.Empty,
        method: String = "GET",
        code: Int = 200,
        fileName: String? = null
    ) = WebServer.expectRequest(
        path, method, code, fileName
    )

    protected fun <R> executeDbOperation(func: suspend WeatherDatabase.() -> R): R {
        return runBlocking {
            func(mocksHandler.mockDb)
        }
    }

}
