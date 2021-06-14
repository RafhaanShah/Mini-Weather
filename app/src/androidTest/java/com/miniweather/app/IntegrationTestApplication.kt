package com.miniweather.app

import com.miniweather.di.AppComponent
import com.miniweather.di.DaggerTestAppComponent

class IntegrationTestApplication : BaseApplication() {

    override lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerTestAppComponent
            .factory()
            .create(applicationContext)
    }

}
