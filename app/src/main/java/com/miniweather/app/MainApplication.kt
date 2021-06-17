package com.miniweather.app

import com.miniweather.di.AppComponent
import com.miniweather.di.DaggerAppComponent

class MainApplication : BaseApplication() {

    override lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .factory()
            .create(applicationContext)
    }
}
