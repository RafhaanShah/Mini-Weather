package com.miniweather.di

import android.content.Context
import com.miniweather.app.BaseDaggerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule(private val app: BaseDaggerApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context = app

}
