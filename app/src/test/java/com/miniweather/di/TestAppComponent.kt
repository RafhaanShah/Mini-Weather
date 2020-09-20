package com.miniweather.di

import com.miniweather.WeatherApplication
import com.miniweather.ui.WeatherActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [TestAppModule::class, TestPresenterModule::class, TestServiceModule::class])
interface TestAppComponent : AppComponent {

    override fun inject(application: WeatherApplication)

    override fun inject(activity: WeatherActivity)

}
