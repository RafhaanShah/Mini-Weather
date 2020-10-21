package com.miniweather.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [TestAppModule::class, TestPresenterModule::class, TestServiceModule::class])
interface TestAppComponent : AppComponent
