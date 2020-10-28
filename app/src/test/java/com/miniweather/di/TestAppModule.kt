package com.miniweather.di

import com.miniweather.service.network.ImageService
import dagger.Module
import dagger.Provides
import org.mockito.Mockito

@Module
interface TestAppModule {

    companion object {

        @Provides
        fun provideImageService(): ImageService = Mockito.mock(ImageService::class.java)

    }

}
