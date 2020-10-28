package com.miniweather.di

import android.content.Context
import android.content.res.Resources
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
interface AppModule {

    companion object {

        @Provides
        fun provideResources(context: Context): Resources = context.resources

        @Provides
        fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

    }

}


