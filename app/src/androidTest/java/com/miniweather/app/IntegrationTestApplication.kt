package com.miniweather.app

import com.miniweather.di.AppComponent
import com.miniweather.di.DaggerAppComponent
import com.miniweather.testutil.WebServer

class IntegrationTestApplication : BaseApplication() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .factory()
            .create(this)
    }

    override fun getAppComponent(): AppComponent {
        return appComponent
    }

    override fun getBaseUrlProvider(): BaseUrlProvider {
        return object : BaseUrlProvider {

            override fun getBaseWeatherUrl(): String {
                return "http://localhost:${WebServer.getPort()}/"
            }

            override fun getBaseImageUrl(): String {
                return "file:///android_asset/images/"
            }
        }
    }

}
