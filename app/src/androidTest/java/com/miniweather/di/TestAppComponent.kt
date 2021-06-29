package com.miniweather.di

import android.content.Context
import com.miniweather.testutil.TestMocksHandler
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        TestDatabaseModule::class,
        NetworkModule::class,
        TestLocationModule::class,
        PresenterModule::class,
        TestMocksModule::class
    ]
)
interface TestAppComponent : AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): TestAppComponent
    }

    fun inject(testMocksHandler: TestMocksHandler)
}
