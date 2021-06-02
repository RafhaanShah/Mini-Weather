package com.miniweather.app

import com.miniweather.di.AppComponent
import org.mockito.Mockito

class TestApplication : BaseApplication() {

    override fun getAppComponent(): AppComponent = Mockito.mock(AppComponent::class.java)

    override fun getBaseUrlProvider(): BaseUrlProvider {
        return object : BaseUrlProvider {

            override fun getBaseWeatherUrl(): String {
                return "http://localhost/"
            }

            override fun getBaseImageUrl(): String {
                return "http://localhost/"
            }
        }
    }

}
