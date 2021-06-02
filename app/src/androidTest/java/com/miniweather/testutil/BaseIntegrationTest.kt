package com.miniweather.testutil

import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miniweather.R
import com.miniweather.service.database.WeatherDatabase
import com.miniweather.service.database.databaseName
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseIntegrationTest<T : Fragment>(private val clazz: Class<T>) {

    private lateinit var scenario: FragmentScenario<T>
    private lateinit var context: Context

    protected lateinit var db: WeatherDatabase

    @CallSuper
    @Before
    open fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.databaseBuilder(
            context,
            WeatherDatabase::class.java, databaseName
        ).build()
        WebServer.start(context.assets)
    }

    @CallSuper
    @After
    open fun tearDown() {
        db.close()
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

    protected fun <S> executeDbOperation(query: suspend () -> S): S = runBlocking { query() }

    protected fun expectHttpRequest(
        path: String = "",
        method: String = "GET",
        code: Int = 200,
        fileName: String? = null
    ) = WebServer.expectRequest(
        path, method, code, fileName
    )

}
