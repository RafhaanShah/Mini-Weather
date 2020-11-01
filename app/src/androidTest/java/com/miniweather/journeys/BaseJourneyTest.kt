package com.miniweather.journeys

import android.content.Context
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miniweather.testutil.FakeWebServer
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseJourneyTest<T : AppCompatActivity>(private val clazz: Class<T>) {

    private lateinit var scenario: ActivityScenario<T>

    protected val context: Context = ApplicationProvider.getApplicationContext()

    protected lateinit var server: FakeWebServer

    @CallSuper
    @Before
    open fun setup(){
        server = FakeWebServer()
        server.start()
    }

    @CallSuper
    @After
    open fun tearDown(){
        server.shutdown()
        server.verifyRequests()
    }

    protected fun launchApp() {
        scenario = ActivityScenario.launch(clazz)
    }

}
