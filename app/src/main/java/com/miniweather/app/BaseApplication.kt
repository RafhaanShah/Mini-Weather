package com.miniweather.app

import android.app.Application
import com.miniweather.di.AppComponent

abstract class BaseApplication : Application() {

    abstract fun getAppComponent(): AppComponent

}
