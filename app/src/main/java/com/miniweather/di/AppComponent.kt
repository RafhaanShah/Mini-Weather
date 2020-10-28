package com.miniweather.di

import com.miniweather.app.BaseApplication
import com.miniweather.ui.weather.WeatherActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        PresenterModule::class,
        ServiceModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        CoroutineModule::class]
)
interface AppComponent {

    fun inject(application: BaseApplication)

    fun inject(activity: WeatherActivity)

}
