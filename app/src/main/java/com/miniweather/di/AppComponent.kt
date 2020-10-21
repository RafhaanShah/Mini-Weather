package com.miniweather.di

import com.miniweather.app.BaseDaggerApplication
import com.miniweather.ui.WeatherActivity
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

    fun inject(application: BaseDaggerApplication)

    fun inject(activity: WeatherActivity)

}
