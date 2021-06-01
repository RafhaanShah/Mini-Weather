package com.miniweather.app

import com.miniweather.di.AppComponent
import com.miniweather.di.DaggerAppComponent

class WeatherApplication : BaseApplication() {

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
                return "https://api.openweathermap.org/"
            }

            override fun getBaseImageUrl(): String {
                return "https://openweathermap.org/img/wn/"
            }
        }
    }

}
