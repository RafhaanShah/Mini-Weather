package com.miniweather.app

import com.miniweather.di.AppComponent
import com.miniweather.di.DaggerAppComponent.factory
import com.miniweather.di.DaggerTestAppComponent

//@Suppress("DEPRECATION")
class TestApplication : BaseApplication() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerTestAppComponent
            .factory()
            .create(this)
    }

    override fun getAppComponent(): AppComponent {
        return appComponent
    }

}
