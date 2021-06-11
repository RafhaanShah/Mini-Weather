package com.miniweather.app

import com.miniweather.di.AppComponent
import io.mockk.mockk

class TestApplication : BaseApplication() {

    override fun getAppComponent(): AppComponent = mockk(relaxed = true)

}
