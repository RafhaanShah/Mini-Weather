package com.miniweather.di

import android.content.Context
import com.miniweather.app.BaseApplication
import com.miniweather.ui.weather.WeatherFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        CoroutineModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        PresenterModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun inject(application: BaseApplication)

    fun inject(fragment: WeatherFragment)

}
