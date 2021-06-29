package com.miniweather.app

import com.miniweather.di.DaggerTestAppComponent
import com.miniweather.di.TestAppComponent

class IntegrationTestApplication : BaseApplication() {

    override lateinit var appComponent: TestAppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerTestAppComponent
            .factory()
            .create(applicationContext)
    }
}
