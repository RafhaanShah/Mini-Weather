package com.miniweather.testutil

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.miniweather.app.IntegrationTestApplication

@Suppress("unused")
class IntegrationTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, IntegrationTestApplication::class.java.name, context)
    }
}
