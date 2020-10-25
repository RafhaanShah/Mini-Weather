package com.miniweather.di

import com.miniweather.app.BaseDaggerApplication
import dagger.Module

@Module
class TestAppModule(app: BaseDaggerApplication) : AppModule(app)
