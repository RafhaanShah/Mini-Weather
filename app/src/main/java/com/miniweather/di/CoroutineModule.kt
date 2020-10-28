package com.miniweather.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
interface CoroutineModule {

    companion object {

        @Provides
        @Named("Main")
        fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

        @Provides
        @Named("IO")
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    }

}
