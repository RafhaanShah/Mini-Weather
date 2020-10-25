package com.miniweather.app

import com.miniweather.di.AppComponent
import com.miniweather.di.DaggerTestAppComponent
import com.miniweather.di.TestAppModule

@Suppress("DEPRECATION")
class TestApplication : BaseDaggerApplication() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerTestAppComponent.builder()
            .testAppModule(TestAppModule(this))
            .build()
    }

    override fun getAppComponent(): AppComponent {
        return appComponent
    }

}
