package com.miniweather.app

import com.miniweather.di.AppComponent
import io.mockk.mockk

class TestApplication : BaseApplication() {

    override val appComponent: AppComponent = mockk(relaxed = true)

}
