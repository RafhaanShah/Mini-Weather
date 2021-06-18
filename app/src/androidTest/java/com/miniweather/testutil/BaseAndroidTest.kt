package com.miniweather.testutil

import android.content.Context
import androidx.annotation.CallSuper
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.Timeout

abstract class BaseAndroidTest {

    private val instrumentationContext: Context = getInstrumentation().context
    protected val appContext: Context = getApplicationContext()
    protected val mocksHandler = TestMocksHandler(appContext, instrumentationContext)

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(30)

    @get:Rule
    val testFailureScreenshotRule = TestFailureScreenshotRule(appContext.contentResolver)

    @get:Rule
    val storagePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @CallSuper
    @Before
    open fun setup() {
        mocksHandler.initialise()
    }

    @CallSuper
    @After
    open fun tearDown() {
        mocksHandler.terminate()
    }
}
