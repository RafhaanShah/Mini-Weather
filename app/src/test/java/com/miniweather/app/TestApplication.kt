package com.miniweather.app

import com.miniweather.di.AppComponent
import org.mockito.Mockito

class TestApplication : BaseApplication() {

    override fun getAppComponent(): AppComponent = Mockito.mock(AppComponent::class.java)

}
