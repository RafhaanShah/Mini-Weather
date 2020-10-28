package com.miniweather.di

import com.miniweather.app.BaseApplication
import dagger.Module

@Module
class TestAppModule(app: BaseApplication) : AppModule(app)
