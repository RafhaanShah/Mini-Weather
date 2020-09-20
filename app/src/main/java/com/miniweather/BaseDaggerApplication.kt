package com.miniweather

import android.app.Application
import com.miniweather.di.AppComponent

// Base Application class so that it can be swapped out in unit tests for injecting mocks using Dagger
abstract class BaseDaggerApplication : Application() {

    abstract fun getAppComponent(): AppComponent

}
