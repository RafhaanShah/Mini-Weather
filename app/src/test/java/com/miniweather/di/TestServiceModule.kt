package com.miniweather.di

import com.miniweather.service.network.ImageService
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import javax.inject.Singleton

@Module
class TestServiceModule {

    @Singleton
    @Provides
    fun provideImageService(): ImageService = Mockito.mock(ImageService::class.java)

}

