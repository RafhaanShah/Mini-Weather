package com.miniweather.di

import android.content.Context
import com.miniweather.app.TestApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TestAppModule(private val app: TestApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context = app

}
