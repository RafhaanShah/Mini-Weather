package com.miniweather.app

import android.app.Application
import com.miniweather.di.AppComponent

// Base Application class so that it can be swapped out in tests for injecting mocks using Dagger
abstract class BaseApplication : Application() {

    abstract fun getAppComponent(): AppComponent

}
