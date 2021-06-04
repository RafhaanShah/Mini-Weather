package com.miniweather.app

import com.miniweather.di.AppComponent
import com.miniweather.di.DaggerTestAppComponent

class IntegrationTestApplication : BaseApplication() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerTestAppComponent
            .factory()
            .create(applicationContext)
    }

    override fun getAppComponent(): AppComponent {
        return appComponent
    }

}
