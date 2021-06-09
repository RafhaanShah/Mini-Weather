package com.miniweather.di

import com.miniweather.provider.BaseUrlProvider
import com.miniweather.provider.DateTimeProvider
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import io.mockk.spyk
import javax.inject.Singleton

@Module
interface TestMocksModule {

    companion object {

        @Singleton
        @Provides
        fun provideMockBaseUrlProvider(): BaseUrlProvider = mockk()

        @Singleton
        @Provides
        fun provideMockTimeService(): DateTimeProvider = spyk(DateTimeProvider())

    }

}
