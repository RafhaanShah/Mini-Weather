package com.miniweather.di

import com.miniweather.WeatherApplication
import com.miniweather.ui.WeatherActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, PresenterModule::class, ServiceModule::class])
interface AppComponent {

    fun inject(application: WeatherApplication)

    fun inject(activity: WeatherActivity)

}
