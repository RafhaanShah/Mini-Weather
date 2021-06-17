package com.miniweather.di

import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
interface TestLocationModule {

    companion object {

        @Singleton
        @Provides
        fun provideFusedLocationProviderClient(): FusedLocationProviderClient = mockk()
    }
}
