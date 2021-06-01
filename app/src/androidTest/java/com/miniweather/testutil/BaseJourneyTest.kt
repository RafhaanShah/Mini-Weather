package com.miniweather.testutil

import android.content.Context
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseJourneyTest<T : AppCompatActivity>(private val clazz: Class<T>) {

    private lateinit var scenario: ActivityScenario<T>

    private val context: Context = ApplicationProvider.getApplicationContext()

    @CallSuper
    @Before
    open fun setup() {
        WebServer.start(context.assets)
    }

    @CallSuper
    @After
    open fun tearDown() {
        WebServer.shutdown()
        WebServer.verifyRequests()
    }

    protected fun launch() {
        scenario = ActivityScenario.launch(clazz)
    }

    protected fun expectHttpRequest(
        path: String = "",
        method: String = "GET",
        code: Int = 200,
        fileName: String? = null
    ) = WebServer.expectRequest(
        path, method, code, fileName
    )

}
